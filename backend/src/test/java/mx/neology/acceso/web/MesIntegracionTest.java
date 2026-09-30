package mx.neology.acceso.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import mx.neology.acceso.soporte.IntegracionBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class MesIntegracionTest extends IntegracionBase {

  private static final Instant CAMBIO = Instant.parse("2026-10-01T06:00:00Z");
  private static final String PERIODO_INICIAL = "00000000-0000-0000-0000-000000000001";

  @BeforeEach
  void fijarReloj() {
    reloj.fijar(CAMBIO);
  }

  private String iniciarMes() throws Exception {
    return mvc.perform(post("/neo/mes/iniciar"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.periodoAnterior.id").value(PERIODO_INICIAL))
        .andExpect(jsonPath("$.periodoActual.fin").value(nullValue()))
        .andReturn()
        .getResponse()
        .getContentAsString();
  }

  private UUID periodoActivo() {
    return jdbc.queryForObject("select id from periodos where activo = true", UUID.class);
  }

  @Test
  void cierraElPeriodoActivoYAbreUnoNuevoSinBorrarNada() throws Exception {
    String json = iniciarMes();
    OffsetDateTime finAnterior =
        OffsetDateTime.parse(
            json.replaceAll(".*\"periodoAnterior\":\\{[^}]*\"fin\":\"([^\"]+)\".*", "$1"));
    assertThat(finAnterior.toInstant()).isEqualTo(CAMBIO);
    sincronizar();
    Integer activos =
        jdbc.queryForObject("select count(*) from periodos where activo = true", Integer.class);
    assertThat(activos).isEqualTo(1);
    assertThat(contar("periodos")).isEqualTo(2);
    assertThat(periodoActivo()).isNotEqualTo(UUID.fromString(PERIODO_INICIAL));
    assertThat(contar("estancias")).isEqualTo(7);
    assertThat(contar("acumulados_residentes")).isEqualTo(3);
    Integer archivadas =
        jdbc.queryForObject(
            "select count(*) from estancias where periodo_id = '" + PERIODO_INICIAL + "'",
            Integer.class);
    assertThat(archivadas).isEqualTo(6);
  }

  @Test
  void unResidenteAcumulaSoloEnElPeriodoNuevo() throws Exception {
    iniciarMes();
    mvc.perform(
            post("/neo/estancias/entrada")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo("RES001")))
        .andExpect(status().isCreated());
    reloj.avanzar(Duration.ofMinutes(20));
    mvc.perform(
            post("/neo/estancias/salida")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo("RES001")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.importe").value(1.00));
    sincronizar();
    UUID nuevo = periodoActivo();
    Map<String, Object> reciente =
        jdbc.queryForMap(
            "select minutos, importe from acumulados_residentes where vehiculo_id ="
                + " '00000000-0000-0000-0000-000000000021' and periodo_id = ?",
            nuevo);
    assertThat(((Number) reciente.get("MINUTOS")).intValue()).isEqualTo(20);
    assertThat((BigDecimal) reciente.get("IMPORTE")).isEqualByComparingTo("1.00");
    Integer anteriores =
        jdbc.queryForObject(
            "select minutos from acumulados_residentes where vehiculo_id ="
                + " '00000000-0000-0000-0000-000000000021' and periodo_id = '"
                + PERIODO_INICIAL
                + "'",
            Integer.class);
    assertThat(anteriores).isEqualTo(210);
    mvc.perform(get("/neo/residentes/pagos").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalMinutos").value(20))
        .andExpect(jsonPath("$.filas[0].placa").value("RES001"))
        .andExpect(jsonPath("$.filas[1].minutos").value(0));
  }

  @Test
  void unaEstanciaAbiertaAntesDelCambioSeArchivaEnElPeriodoNuevo() throws Exception {
    iniciarMes();
    reloj.avanzar(Duration.ofHours(1));
    mvc.perform(
            post("/neo/estancias/salida")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo("OFI002")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.importe").value(0.00))
        .andExpect(jsonPath("$.momentoCobro").value("NINGUNO"));
    sincronizar();
    UUID periodoEstancia =
        jdbc.queryForObject(
            "select periodo_id from estancias where id = '00000000-0000-0000-0000-000000000107'",
            UUID.class);
    assertThat(periodoEstancia).isEqualTo(periodoActivo());
  }

  @Test
  void unSegundoPeriodoActivoViolaLaRestriccionUnica() {
    assertThatThrownBy(
            () ->
                jdbc.update(
                    "insert into periodos (id, inicio, fin, activo, created_at, updated_at)"
                        + " values (?, ?, null, true, ?, ?)",
                    UUID.randomUUID(),
                    OffsetDateTime.now(reloj),
                    OffsetDateTime.now(reloj),
                    OffsetDateTime.now(reloj)))
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasMessageContaining("UQ_PERIODOS_ACTIVO");
  }
}
