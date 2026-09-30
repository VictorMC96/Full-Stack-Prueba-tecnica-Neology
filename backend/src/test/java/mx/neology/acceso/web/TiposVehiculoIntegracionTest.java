package mx.neology.acceso.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.Instant;
import mx.neology.acceso.soporte.IntegracionBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class TiposVehiculoIntegracionTest extends IntegracionBase {

  private static final String VISITANTE =
      "{\"codigo\":\"visitante\",\"nombre\":\"Visitante\","
          + "\"modeloCobro\":\"POR_MINUTO_AL_SALIR\",\"tarifaPorMinuto\":0.30}";

  @BeforeEach
  void fijarReloj() {
    reloj.fijar(Instant.parse("2026-09-15T18:00:00Z"));
  }

  private void crearVisitante() throws Exception {
    mvc.perform(
            post("/neo/tipos-vehiculo").contentType(MediaType.APPLICATION_JSON).content(VISITANTE))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/neo/tipos-vehiculo/VISITANTE"))
        .andExpect(jsonPath("$.codigo").value("VISITANTE"))
        .andExpect(jsonPath("$.nombre").value("Visitante"))
        .andExpect(jsonPath("$.modeloCobro").value("POR_MINUTO_AL_SALIR"))
        .andExpect(jsonPath("$.tarifaPorMinuto").value(0.30))
        .andExpect(jsonPath("$.provisional").value(false))
        .andExpect(jsonPath("$.integrado").value(false))
        .andExpect(jsonPath("$.vehiculos").value(0));
  }

  @Test
  void elCatalogoInicialTieneLosTresTiposDelEnunciado() throws Exception {
    mvc.perform(get("/neo/tipos-vehiculo"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$[0].codigo").value("NO_RESIDENTE"))
        .andExpect(jsonPath("$[0].provisional").value(true))
        .andExpect(jsonPath("$[0].vehiculos").value(1))
        .andExpect(jsonPath("$[1].codigo").value("OFICIAL"))
        .andExpect(jsonPath("$[1].modeloCobro").value("SIN_COBRO"))
        .andExpect(jsonPath("$[1].vehiculos").value(2))
        .andExpect(jsonPath("$[2].codigo").value("RESIDENTE"))
        .andExpect(jsonPath("$[2].tarifaPorMinuto").value(0.05))
        .andExpect(jsonPath("$[2].integrado").value(true))
        .andExpect(jsonPath("$[2].vehiculos").value(3));
    assertThat(contar("tipos_vehiculo")).isEqualTo(3);
  }

  @Test
  void unTipoCreadoPorHttpCobraConSuTarifaAlSalir() throws Exception {
    crearVisitante();
    mvc.perform(
            post("/neo/vehiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"placa\":\"vis-001\",\"tipo\":\"visitante\"}"))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/neo/vehiculos/VIS001"))
        .andExpect(jsonPath("$.tipo").value("VISITANTE"))
        .andExpect(jsonPath("$.tipoNombre").value("Visitante"));
    mvc.perform(
            post("/neo/estancias/entrada")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo("VIS001")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.tipo").value("VISITANTE"))
        .andExpect(jsonPath("$.momentoCobro").value("AL_SALIR"));
    reloj.avanzar(Duration.ofMinutes(3));
    mvc.perform(
            post("/neo/estancias/salida")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo("VIS001")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.minutos").value(3))
        .andExpect(jsonPath("$.importe").value(0.90))
        .andExpect(jsonPath("$.tipoNombre").value("Visitante"))
        .andExpect(jsonPath("$.momentoCobro").value("AL_SALIR"));
    mvc.perform(get("/neo/vehiculos/VIS001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tipoNombre").value("Visitante"))
        .andExpect(jsonPath("$.acumuladoPeriodo").doesNotExist());
    mvc.perform(get("/neo/tipos-vehiculo/VISITANTE"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.vehiculos").value(1));
    mvc.perform(delete("/neo/tipos-vehiculo/VISITANTE"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("TIPO_EN_USO"));
  }

  @Test
  void unTipoAcumuladoNuevoEntraEnElInformeDePagos() throws Exception {
    mvc.perform(
            post("/neo/tipos-vehiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"codigo\":\"PENSION\",\"nombre\":\"Pensión\","
                        + "\"modeloCobro\":\"POR_MINUTO_ACUMULADO\",\"tarifaPorMinuto\":0.02}"))
        .andExpect(status().isCreated());
    mvc.perform(
            post("/neo/vehiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"placa\":\"PEN001\",\"tipo\":\"PENSION\"}"))
        .andExpect(status().isCreated());
    mvc.perform(
            post("/neo/estancias/entrada")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo("PEN001")))
        .andExpect(status().isCreated());
    reloj.avanzar(Duration.ofMinutes(50));
    mvc.perform(
            post("/neo/estancias/salida")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo("PEN001")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.importe").value(1.00))
        .andExpect(jsonPath("$.momentoCobro").value("INFORME_MENSUAL"));
    mvc.perform(get("/neo/vehiculos/PEN001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.acumuladoPeriodo.minutos").value(50))
        .andExpect(jsonPath("$.acumuladoPeriodo.importe").value(1.00));
    mvc.perform(get("/neo/residentes/pagos").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.filas.length()").value(4))
        .andExpect(jsonPath("$.filas[0].placa").value("PEN001"))
        .andExpect(jsonPath("$.filas[0].minutos").value(50))
        .andExpect(jsonPath("$.totalMinutos").value(350))
        .andExpect(jsonPath("$.totalImporte").value(16.00));
  }

  @Test
  void actualizaYEliminaUnTipoCreadoYProtegeLosIntegrados() throws Exception {
    crearVisitante();
    mvc.perform(
            post("/neo/tipos-vehiculo").contentType(MediaType.APPLICATION_JSON).content(VISITANTE))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("TIPO_YA_REGISTRADO"));
    mvc.perform(
            put("/neo/tipos-vehiculo/visitante")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"nombre\":\"Visita\",\"modeloCobro\":\"POR_MINUTO_ACUMULADO\","
                        + "\"tarifaPorMinuto\":0.10}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nombre").value("Visita"))
        .andExpect(jsonPath("$.modeloCobro").value("POR_MINUTO_ACUMULADO"))
        .andExpect(jsonPath("$.tarifaPorMinuto").value(0.10));
    sincronizar();
    assertThat(
            jdbc.queryForObject(
                "select nombre from tipos_vehiculo where codigo = 'VISITANTE'", String.class))
        .isEqualTo("Visita");
    mvc.perform(delete("/neo/tipos-vehiculo/VISITANTE")).andExpect(status().isNoContent());
    sincronizar();
    assertThat(contar("tipos_vehiculo")).isEqualTo(3);
    mvc.perform(delete("/neo/tipos-vehiculo/VISITANTE"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("TIPO_NO_ENCONTRADO"));

    mvc.perform(
            put("/neo/tipos-vehiculo/RESIDENTE")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"nombre\":\"Residente\",\"modeloCobro\":\"POR_MINUTO_AL_SALIR\","
                        + "\"tarifaPorMinuto\":0.05}"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("TIPO_INTEGRADO"));
    mvc.perform(
            put("/neo/tipos-vehiculo/RESIDENTE")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"nombre\":\"Residente\",\"modeloCobro\":\"POR_MINUTO_ACUMULADO\","
                        + "\"tarifaPorMinuto\":0.07}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tarifaPorMinuto").value(0.07))
        .andExpect(jsonPath("$.vehiculos").value(3));
    mvc.perform(delete("/neo/tipos-vehiculo/OFICIAL"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("TIPO_INTEGRADO"));
    mvc.perform(
            post("/neo/tipos-vehiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"codigo\":\"GRATIS\",\"nombre\":\"Gratis\",\"modeloCobro\":\"SIN_COBRO\","
                        + "\"tarifaPorMinuto\":0.10}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDACION"))
        .andExpect(jsonPath("$.errors[0].field").value("tarifaPorMinuto"));
    mvc.perform(
            post("/neo/vehiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"placa\":\"ABC123\",\"tipo\":\"FANTASMA\"}"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("TIPO_NO_ENCONTRADO"));
  }
}
