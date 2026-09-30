package mx.neology.acceso.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.neology.acceso.application.IniciarMes;
import mx.neology.acceso.application.modelo.ResultadoIniciarMes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/neo/mes")
@Tag(name = "Mes", description = "Cierre del periodo activo y apertura del siguiente")
public class MesController {

  private final IniciarMes iniciarMes;

  public MesController(IniciarMes iniciarMes) {
    this.iniciarMes = iniciarMes;
  }

  @PostMapping("/iniciar")
  @Operation(summary = "Iniciar un nuevo mes: cierra el periodo activo y abre uno nuevo")
  public IniciarMesResponse iniciar() {
    ResultadoIniciarMes resultado = iniciarMes.ejecutar();
    return new IniciarMesResponse(
        Respuestas.periodo(resultado.periodoAnterior()),
        Respuestas.periodo(resultado.periodoActual()));
  }
}
