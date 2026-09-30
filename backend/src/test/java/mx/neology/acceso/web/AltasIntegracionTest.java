package mx.neology.acceso.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import mx.neology.acceso.soporte.IntegracionBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class AltasIntegracionTest extends IntegracionBase {

  @Test
  void altaDePlacaDesconocidaDevuelve201ConLocation() throws Exception {
    mvc.perform(
            post("/neo/vehiculos/oficiales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo("abc-123")))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/neo/vehiculos/ABC123"))
        .andExpect(jsonPath("$.placa").value("ABC123"))
        .andExpect(jsonPath("$.tipo").value("OFICIAL"))
        .andExpect(jsonPath("$.dentro").value(false));
    sincronizar();
    assertThat(contar("vehiculos")).isEqualTo(7);
  }

  @Test
  void altaDePlacaYaOficialOResidenteDevuelve409() throws Exception {
    for (String placa : new String[] {"OFI001", "RES001"}) {
      mvc.perform(
              post("/neo/vehiculos/residentes")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(cuerpo(placa)))
          .andExpect(status().isConflict())
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
          .andExpect(jsonPath("$.code").value("VEHICULO_YA_REGISTRADO"));
    }
    sincronizar();
    assertThat(contar("vehiculos")).isEqualTo(6);
  }

  @Test
  void altaDeUnNoResidenteExistenteLoReclasificaConservandoElId() throws Exception {
    mvc.perform(
            post("/neo/vehiculos/residentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo("nor-001")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("00000000-0000-0000-0000-000000000031"))
        .andExpect(jsonPath("$.tipo").value("RESIDENTE"));
    sincronizar();
    String tipo =
        jdbc.queryForObject("select tipo from vehiculos where placa = 'NOR001'", String.class);
    assertThat(tipo).isEqualTo("RESIDENTE");
    assertThat(contar("vehiculos")).isEqualTo(6);
  }

  @Test
  void altaDeNoResidenteConPlacaExistenteDevuelve409() throws Exception {
    for (String placa : new String[] {"NOR001", "OFI001", "RES002"}) {
      mvc.perform(
              post("/neo/vehiculos/no-residentes")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(cuerpo(placa)))
          .andExpect(status().isConflict())
          .andExpect(jsonPath("$.code").value("VEHICULO_YA_REGISTRADO"));
    }
  }

  @Test
  void placaInvalidaDevuelve400ValidacionConErrors() throws Exception {
    for (String cuerpo : new String[] {"{}", cuerpo(" "), cuerpo("a-b"), cuerpo("ABC<1>")}) {
      mvc.perform(
              post("/neo/vehiculos/oficiales")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(cuerpo))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
          .andExpect(jsonPath("$.code").value("VALIDACION"))
          .andExpect(jsonPath("$.errors[0].field").value("placa"));
    }
  }
}
