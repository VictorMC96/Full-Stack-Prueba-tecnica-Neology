package mx.neology.acceso.web;

import static org.assertj.core.api.Assertions.assertThat;
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
import mx.neology.acceso.soporte.IntegracionBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class EstanciasIntegracionTest extends IntegracionBase {

  private static final Instant INICIO = Instant.parse("2026-09-15T18:00:00Z");

  @BeforeEach
  void fijarReloj() {
    reloj.fijar(INICIO);
  }

  private MvcResult entrada(String placa) throws Exception {
    return mvc.perform(
            post("/neo/estancias/entrada")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo(placa)))
        .andExpect(status().isCreated())
        .andReturn();
  }

  @Test
  void entradaDePlacaDesconocidaLaCreaComoNoResidente() throws Exception {
    MvcResult resultado = entrada("nuevo-01");
    String json = resultado.getResponse().getContentAsString();
    assertThat(json).contains("\"placa\":\"NUEVO01\"").contains("\"tipo\":\"NO_RESIDENTE\"");
    mvc.perform(get("/neo/vehiculos/NUEVO01"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.dentro").value(true))
        .andExpect(jsonPath("$.estanciaAbierta.salida").value(nullValue()))
        .andExpect(jsonPath("$.estanciaAbierta.minutos").value(nullValue()))
        .andExpect(jsonPath("$.estanciaAbierta.importe").value(nullValue()))
        .andExpect(jsonPath("$.estanciaAbierta.momentoCobro").value("AL_SALIR"))
        .andExpect(jsonPath("$.acumuladoPeriodo").value(nullValue()));
    sincronizar();
    String tipo =
        jdbc.queryForObject("select tipo from vehiculos where placa = 'NUEVO01'", String.class);
    assertThat(tipo).isEqualTo("NO_RESIDENTE");
    OffsetDateTime entradaGuardada =
        jdbc.queryForObject(
            "select e.entrada from estancias e join vehiculos v on v.id = e.vehiculo_id"
                + " where v.placa = 'NUEVO01'",
            OffsetDateTime.class);
    assertThat(entradaGuardada.toInstant()).isEqualTo(INICIO);
  }

  @Test
  void entradaDeVehiculoQueYaEstaDentroDevuelve409() throws Exception {
    mvc.perform(
            post("/neo/estancias/entrada")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo("OFI002")))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("VEHICULO_YA_DENTRO"));
    sincronizar();
    assertThat(contar("estancias")).isEqualTo(7);
  }

  @Test
  void salidaDeNoResidenteCobraTodoMinutoIniciadoAlSalir() throws Exception {
    entrada("NOR001");
    reloj.avanzar(Duration.ofMinutes(10).plusSeconds(5));

    mvc.perform(
            post("/neo/estancias/salida")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo("nor001")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.placa").value("NOR001"))
        .andExpect(jsonPath("$.minutos").value(11))
        .andExpect(jsonPath("$.importe").value(5.50))
        .andExpect(jsonPath("$.momentoCobro").value("AL_SALIR"))
        .andExpect(jsonPath("$.periodoId").value("00000000-0000-0000-0000-000000000001"));
    sincronizar();
    Map<String, Object> fila =
        jdbc.queryForMap("select estancia_abierta_id from vehiculos where placa = 'NOR001'");
    assertThat(fila.get("ESTANCIA_ABIERTA_ID")).isNull();
    OffsetDateTime salida =
        jdbc.queryForObject(
            "select max(salida) from estancias e join vehiculos v on v.id = e.vehiculo_id"
                + " where v.placa = 'NOR001'",
            OffsetDateTime.class);
    assertThat(salida.toInstant()).isEqualTo(INICIO.plus(Duration.ofMinutes(10).plusSeconds(5)));
  }

  @Test
  void salidaDeResidenteAcumulaEnElPeriodoActivo() throws Exception {
    entrada("RES001");
    reloj.avanzar(Duration.ofMinutes(11));

    mvc.perform(
            post("/neo/estancias/salida")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo("RES001")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.minutos").value(11))
        .andExpect(jsonPath("$.importe").value(0.55))
        .andExpect(jsonPath("$.momentoCobro").value("INFORME_MENSUAL"));
    sincronizar();
    Map<String, Object> acumulado =
        jdbc.queryForMap(
            "select minutos, importe from acumulados_residentes where vehiculo_id ="
                + " '00000000-0000-0000-0000-000000000021' and periodo_id ="
                + " '00000000-0000-0000-0000-000000000001'");
    assertThat(((Number) acumulado.get("MINUTOS")).intValue()).isEqualTo(221);
    assertThat((BigDecimal) acumulado.get("IMPORTE")).isEqualByComparingTo("11.05");
    assertThat(contar("acumulados_residentes")).isEqualTo(3);
  }

  @Test
  void salidaSinEntradaDevuelve409() throws Exception {
    mvc.perform(
            post("/neo/estancias/salida")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo("RES002")))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("VEHICULO_NO_DENTRO"));
  }

  @Test
  void salidaDePlacaDesconocidaDevuelve404() throws Exception {
    mvc.perform(
            post("/neo/estancias/salida")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo("ZZZ999")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("VEHICULO_NO_ENCONTRADO"));
  }
}
