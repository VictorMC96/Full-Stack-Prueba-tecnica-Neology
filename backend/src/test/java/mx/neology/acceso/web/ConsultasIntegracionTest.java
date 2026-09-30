package mx.neology.acceso.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import mx.neology.acceso.soporte.IntegracionBase;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

class ConsultasIntegracionTest extends IntegracionBase {

  @Test
  void listadoPaginadoYOrdenadoPorPlaca() throws Exception {
    mvc.perform(
            get("/neo/vehiculos").param("page", "0").param("size", "2").param("sort", "placa,asc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].placa").value("NOR001"))
        .andExpect(jsonPath("$.content[1].placa").value("OFI001"))
        .andExpect(jsonPath("$.content[0].tipo").value("NO_RESIDENTE"))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(2))
        .andExpect(jsonPath("$.totalElements").value(6))
        .andExpect(jsonPath("$.totalPages").value(3));
    mvc.perform(get("/neo/vehiculos").param("sort", "placa,desc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].placa").value("RES003"));
  }

  @Test
  void filtroPorPlacaSinDistinguirMayusculas() throws Exception {
    mvc.perform(get("/neo/vehiculos").param("placa", "res"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(3))
        .andExpect(jsonPath("$.content[0].placa").value("RES001"))
        .andExpect(jsonPath("$.content[1].placa").value("RES002"))
        .andExpect(jsonPath("$.content[2].placa").value("RES003"));
  }

  @Test
  void tamanoSeRecortaACienYParametrosInvalidosDan400() throws Exception {
    mvc.perform(get("/neo/vehiculos").param("size", "500"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size").value(100));
    for (String[] parametro :
        new String[][] {{"sort", "color,asc"}, {"page", "-1"}, {"size", "0"}}) {
      mvc.perform(get("/neo/vehiculos").param(parametro[0], parametro[1]))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.code").value("PARAMETRO_INVALIDO"));
    }
  }

  @Test
  void detalleDeResidenteYPlacaDesconocida() throws Exception {
    mvc.perform(get("/neo/vehiculos/RES001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tipo").value("RESIDENTE"))
        .andExpect(jsonPath("$.dentro").value(false))
        .andExpect(jsonPath("$.estanciaAbierta").value(nullValue()))
        .andExpect(jsonPath("$.acumuladoPeriodo.minutos").value(210))
        .andExpect(jsonPath("$.acumuladoPeriodo.importe").value(10.50));
    mvc.perform(get("/neo/vehiculos/OFI002"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.dentro").value(true))
        .andExpect(jsonPath("$.estanciaAbierta.id").value("00000000-0000-0000-0000-000000000107"))
        .andExpect(jsonPath("$.estanciaAbierta.momentoCobro").value("NINGUNO"))
        .andExpect(jsonPath("$.acumuladoPeriodo").value(nullValue()));
    mvc.perform(get("/neo/vehiculos/ZZZ999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("VEHICULO_NO_ENCONTRADO"));
  }

  @Test
  void historialDeEstanciasDelMasRecienteAlMasAntiguo() throws Exception {
    MvcResult resultado =
        mvc.perform(get("/neo/vehiculos/RES001/estancias"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.content[0].id").value("00000000-0000-0000-0000-000000000102"))
            .andExpect(jsonPath("$.content[1].id").value("00000000-0000-0000-0000-000000000101"))
            .andExpect(jsonPath("$.content[0].minutos").value(120))
            .andExpect(jsonPath("$.content[0].importe").value(6.00))
            .andExpect(jsonPath("$.content[0].momentoCobro").value("INFORME_MENSUAL"))
            .andReturn();
    String json = resultado.getResponse().getContentAsString();
    OffsetDateTime primera =
        OffsetDateTime.parse(json.replaceAll(".*?\"entrada\":\"([^\"]+)\".*", "$1"));
    assertThat(primera).isEqualTo(OffsetDateTime.parse("2026-09-03T08:00:00-06:00"));
    mvc.perform(get("/neo/vehiculos/RES001/estancias").param("page", "1").param("size", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value("00000000-0000-0000-0000-000000000101"))
        .andExpect(jsonPath("$.totalPages").value(2));
  }
}
