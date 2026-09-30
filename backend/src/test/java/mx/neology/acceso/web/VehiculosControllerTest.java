package mx.neology.acceso.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.util.UUID;
import mx.neology.acceso.application.ConsultarVehiculos;
import mx.neology.acceso.application.RegistrarVehiculo;
import mx.neology.acceso.domain.Placa;
import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.Vehiculo;
import mx.neology.acceso.domain.excepcion.PlacaInvalidaException;
import mx.neology.acceso.domain.excepcion.VehiculoYaRegistradoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(VehiculosController.class)
class VehiculosControllerTest {

  @Autowired MockMvc mvc;
  @MockitoBean RegistrarVehiculo registrarVehiculo;
  @MockitoBean ConsultarVehiculos consultarVehiculos;

  @Test
  void altaDevuelve201ConLocationYCuerpo() throws Exception {
    OffsetDateTime ahora = OffsetDateTime.parse("2026-09-15T12:00:00-06:00");
    Vehiculo creado =
        new Vehiculo(
            UUID.randomUUID(), Placa.de("ABC123"), TipoVehiculo.OFICIAL, null, ahora, ahora);
    when(registrarVehiculo.ejecutar("abc-123", TipoVehiculo.OFICIAL)).thenReturn(creado);

    mvc.perform(
            post("/neo/vehiculos/oficiales")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"placa\":\"abc-123\"}"))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/neo/vehiculos/ABC123"))
        .andExpect(jsonPath("$.placa").value("ABC123"))
        .andExpect(jsonPath("$.tipo").value("OFICIAL"))
        .andExpect(jsonPath("$.dentro").value(false))
        .andExpect(jsonPath("$.id").value(creado.id().toString()));
  }

  @Test
  void conflictoDeNegocioSaleComoProblemDetail409() throws Exception {
    when(registrarVehiculo.ejecutar(any(), eq(TipoVehiculo.RESIDENTE)))
        .thenThrow(new VehiculoYaRegistradoException("OFI001"));

    mvc.perform(
            post("/neo/vehiculos/residentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"placa\":\"OFI001\"}"))
        .andExpect(status().isConflict())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.code").value("VEHICULO_YA_REGISTRADO"))
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.instance").value("/neo/vehiculos/residentes"));
  }

  @Test
  void placaEnBlancoSaleComoValidacion400ConErrors() throws Exception {
    mvc.perform(
            post("/neo/vehiculos/no-residentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"placa\":\"  \"}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.code").value("VALIDACION"))
        .andExpect(jsonPath("$.errors[0].field").value("placa"));
  }

  @Test
  void placaConCaracteresInvalidosSaleComoValidacion400() throws Exception {
    when(registrarVehiculo.ejecutar(any(), any())).thenThrow(new PlacaInvalidaException("ABC<1>"));

    mvc.perform(
            post("/neo/vehiculos/oficiales")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"placa\":\"ABC<1>\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDACION"))
        .andExpect(jsonPath("$.errors[0].field").value("placa"));
  }

  @Test
  void cuerpoIlegibleSaleComoValidacion400() throws Exception {
    mvc.perform(
            post("/neo/vehiculos/oficiales")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{placa"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDACION"));
  }

  @Test
  void parametrosDePaginaInvalidosSalenComoParametroInvalido400() throws Exception {
    mvc.perform(get("/neo/vehiculos").param("sort", "color,asc"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("PARAMETRO_INVALIDO"));
    mvc.perform(get("/neo/vehiculos").param("page", "-1"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("PARAMETRO_INVALIDO"));
    mvc.perform(get("/neo/vehiculos").param("size", "0"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("PARAMETRO_INVALIDO"));
    mvc.perform(get("/neo/vehiculos").param("sort", "placa,sideways"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("PARAMETRO_INVALIDO"));
    mvc.perform(get("/neo/vehiculos").param("page", "abc"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("PARAMETRO_INVALIDO"));
  }

  @Test
  void errorInesperadoSaleComo500SinTraza() throws Exception {
    when(registrarVehiculo.ejecutar(any(), any())).thenThrow(new IllegalStateException("boom"));

    mvc.perform(
            post("/neo/vehiculos/oficiales")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"placa\":\"ABC123\"}"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code").value("ERROR_INTERNO"))
        .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.not("boom")));
  }
}
