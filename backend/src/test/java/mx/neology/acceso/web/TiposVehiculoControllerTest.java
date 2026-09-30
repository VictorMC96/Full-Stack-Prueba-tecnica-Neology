package mx.neology.acceso.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import mx.neology.acceso.application.ActualizarTipoVehiculo;
import mx.neology.acceso.application.ConsultarTiposVehiculo;
import mx.neology.acceso.application.EliminarTipoVehiculo;
import mx.neology.acceso.application.RegistrarTipoVehiculo;
import mx.neology.acceso.application.modelo.TipoVehiculoConUso;
import mx.neology.acceso.domain.CodigoTipo;
import mx.neology.acceso.domain.ModeloCobro;
import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.excepcion.TipoEnUsoException;
import mx.neology.acceso.domain.excepcion.TipoIntegradoException;
import mx.neology.acceso.domain.excepcion.TipoNoEncontradoException;
import mx.neology.acceso.domain.excepcion.TipoYaRegistradoException;
import mx.neology.acceso.domain.excepcion.ValidacionException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TiposVehiculoController.class)
class TiposVehiculoControllerTest {

  private static final OffsetDateTime AHORA = OffsetDateTime.parse("2026-09-15T12:00:00-06:00");
  private static final TipoVehiculo VISITANTE =
      new TipoVehiculo(
          new CodigoTipo("VISITANTE"),
          "Visitante",
          ModeloCobro.POR_MINUTO_AL_SALIR,
          new BigDecimal("0.30"),
          false,
          false,
          AHORA,
          AHORA);
  private static final TipoVehiculo OFICIAL =
      new TipoVehiculo(
          new CodigoTipo("OFICIAL"),
          "Oficial",
          ModeloCobro.SIN_COBRO,
          new BigDecimal("0.00"),
          false,
          true,
          AHORA,
          AHORA);

  @Autowired MockMvc mvc;
  @MockitoBean ConsultarTiposVehiculo consultar;
  @MockitoBean RegistrarTipoVehiculo registrar;
  @MockitoBean ActualizarTipoVehiculo actualizar;
  @MockitoBean EliminarTipoVehiculo eliminar;

  @Test
  void listaElCatalogoConUso() throws Exception {
    when(consultar.listar())
        .thenReturn(
            List.of(new TipoVehiculoConUso(OFICIAL, 2), new TipoVehiculoConUso(VISITANTE, 0)));

    mvc.perform(get("/neo/tipos-vehiculo"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].codigo").value("OFICIAL"))
        .andExpect(jsonPath("$[0].nombre").value("Oficial"))
        .andExpect(jsonPath("$[0].modeloCobro").value("SIN_COBRO"))
        .andExpect(jsonPath("$[0].tarifaPorMinuto").value(0.00))
        .andExpect(jsonPath("$[0].provisional").value(false))
        .andExpect(jsonPath("$[0].integrado").value(true))
        .andExpect(jsonPath("$[0].vehiculos").value(2))
        .andExpect(jsonPath("$[1].codigo").value("VISITANTE"))
        .andExpect(jsonPath("$[1].tarifaPorMinuto").value(0.30));

    when(consultar.buscar("VISITANTE")).thenReturn(new TipoVehiculoConUso(VISITANTE, 1));
    mvc.perform(get("/neo/tipos-vehiculo/VISITANTE"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.vehiculos").value(1));

    when(consultar.buscar("ZZZ")).thenThrow(new TipoNoEncontradoException("ZZZ"));
    mvc.perform(get("/neo/tipos-vehiculo/ZZZ"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("TIPO_NO_ENCONTRADO"));
  }

  @Test
  void creaUnTipoCon201YLocation() throws Exception {
    when(registrar.ejecutar(
            "visitante", "Visitante", ModeloCobro.POR_MINUTO_AL_SALIR, new BigDecimal("0.30")))
        .thenReturn(VISITANTE);

    mvc.perform(
            post("/neo/tipos-vehiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"codigo\":\"visitante\",\"nombre\":\"Visitante\","
                        + "\"modeloCobro\":\"POR_MINUTO_AL_SALIR\",\"tarifaPorMinuto\":0.30}"))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/neo/tipos-vehiculo/VISITANTE"))
        .andExpect(jsonPath("$.codigo").value("VISITANTE"))
        .andExpect(jsonPath("$.vehiculos").value(0));
  }

  @Test
  void creacionInvalidaDevuelve400ConErrorsYDuplicado409() throws Exception {
    mvc.perform(
            post("/neo/tipos-vehiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"codigo\":\"AB\",\"nombre\":\"X\",\"tarifaPorMinuto\":-1}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDACION"))
        .andExpect(jsonPath("$.errors.length()").value(4));

    mvc.perform(
            post("/neo/tipos-vehiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"codigo\":\"NUEVO\",\"nombre\":\"Nuevo\",\"modeloCobro\":\"SIN_COBRO\","
                        + "\"tarifaPorMinuto\":0.123}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDACION"))
        .andExpect(jsonPath("$.errors[0].field").value("tarifaPorMinuto"));

    when(registrar.ejecutar(eq("NUEVO"), any(), eq(ModeloCobro.SIN_COBRO), any()))
        .thenThrow(
            new ValidacionException("tarifaPorMinuto", "Un tipo sin cobro debe tener tarifa 0.00"));
    mvc.perform(
            post("/neo/tipos-vehiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"codigo\":\"NUEVO\",\"nombre\":\"Nuevo\",\"modeloCobro\":\"SIN_COBRO\","
                        + "\"tarifaPorMinuto\":0.10}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDACION"))
        .andExpect(jsonPath("$.errors[0].field").value("tarifaPorMinuto"));

    when(registrar.ejecutar(eq("OFICIAL"), any(), any(), any()))
        .thenThrow(new TipoYaRegistradoException("OFICIAL"));
    mvc.perform(
            post("/neo/tipos-vehiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"codigo\":\"OFICIAL\",\"nombre\":\"Oficial\",\"modeloCobro\":\"SIN_COBRO\","
                        + "\"tarifaPorMinuto\":0}"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("TIPO_YA_REGISTRADO"));
  }

  @Test
  void actualizaYEliminaConLosCodigosDeError() throws Exception {
    when(actualizar.ejecutar(
            "VISITANTE", "Visita", ModeloCobro.POR_MINUTO_AL_SALIR, new BigDecimal("0.40")))
        .thenReturn(new TipoVehiculoConUso(VISITANTE, 1));
    mvc.perform(
            put("/neo/tipos-vehiculo/VISITANTE")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"nombre\":\"Visita\",\"modeloCobro\":\"POR_MINUTO_AL_SALIR\","
                        + "\"tarifaPorMinuto\":0.40}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.codigo").value("VISITANTE"))
        .andExpect(jsonPath("$.vehiculos").value(1));

    when(actualizar.ejecutar(eq("OFICIAL"), any(), eq(ModeloCobro.POR_MINUTO_AL_SALIR), any()))
        .thenThrow(new TipoIntegradoException("OFICIAL", "admite cambiar su modelo de cobro"));
    mvc.perform(
            put("/neo/tipos-vehiculo/OFICIAL")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"nombre\":\"Oficial\",\"modeloCobro\":\"POR_MINUTO_AL_SALIR\","
                        + "\"tarifaPorMinuto\":0.10}"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("TIPO_INTEGRADO"));

    when(actualizar.ejecutar(eq("ZZZ"), any(), any(), any()))
        .thenThrow(new TipoNoEncontradoException("ZZZ"));
    mvc.perform(
            put("/neo/tipos-vehiculo/ZZZ")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"nombre\":\"Nada\",\"modeloCobro\":\"SIN_COBRO\",\"tarifaPorMinuto\":0}"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("TIPO_NO_ENCONTRADO"));

    mvc.perform(delete("/neo/tipos-vehiculo/VISITANTE")).andExpect(status().isNoContent());
    verify(eliminar).ejecutar("VISITANTE");

    doThrow(new TipoIntegradoException("OFICIAL", "puede eliminarse"))
        .when(eliminar)
        .ejecutar("OFICIAL");
    mvc.perform(delete("/neo/tipos-vehiculo/OFICIAL"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("TIPO_INTEGRADO"));

    doThrow(new TipoEnUsoException("PENSION", 2)).when(eliminar).ejecutar("PENSION");
    mvc.perform(delete("/neo/tipos-vehiculo/PENSION"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("TIPO_EN_USO"));

    doThrow(new TipoNoEncontradoException("ZZZ")).when(eliminar).ejecutar("ZZZ");
    mvc.perform(delete("/neo/tipos-vehiculo/ZZZ"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("TIPO_NO_ENCONTRADO"));
  }
}
