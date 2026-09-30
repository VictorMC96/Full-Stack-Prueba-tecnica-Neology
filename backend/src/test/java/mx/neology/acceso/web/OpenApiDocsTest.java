package mx.neology.acceso.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import mx.neology.acceso.soporte.IntegracionBase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class OpenApiDocsTest extends IntegracionBase {

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/neo/vehiculos/oficiales",
        "/neo/vehiculos/residentes",
        "/neo/vehiculos/no-residentes",
        "/neo/estancias/entrada",
        "/neo/estancias/salida",
        "/neo/residentes/pagos",
        "/neo/mes/iniciar",
        "/neo/vehiculos",
        "/neo/vehiculos/{placa}",
        "/neo/vehiculos/{placa}/estancias"
      })
  void documentaCadaRuta(String ruta) throws Exception {
    mvc.perform(get("/v3/api-docs"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.paths['" + ruta + "']").exists());
  }
}
