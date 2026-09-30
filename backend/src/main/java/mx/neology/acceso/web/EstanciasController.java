package mx.neology.acceso.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.neology.acceso.application.RegistrarEntrada;
import mx.neology.acceso.application.RegistrarSalida;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/neo/estancias")
@Tag(name = "Estancias", description = "Entradas y salidas del estacionamiento")
public class EstanciasController {

  private final RegistrarEntrada registrarEntrada;
  private final RegistrarSalida registrarSalida;

  public EstanciasController(RegistrarEntrada registrarEntrada, RegistrarSalida registrarSalida) {
    this.registrarEntrada = registrarEntrada;
    this.registrarSalida = registrarSalida;
  }

  @PostMapping("/entrada")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Registrar la entrada de un vehículo (una placa desconocida se da de alta)")
  public EstanciaResponse entrada(@Valid @RequestBody PlacaRequest cuerpo) {
    return Respuestas.estancia(registrarEntrada.ejecutar(cuerpo.placa()));
  }

  @PostMapping("/salida")
  @Operation(summary = "Registrar la salida de un vehículo y calcular su cobro")
  public EstanciaResponse salida(@Valid @RequestBody PlacaRequest cuerpo) {
    return Respuestas.estancia(registrarSalida.ejecutar(cuerpo.placa()));
  }
}
