package mx.neology.acceso.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Set;
import mx.neology.acceso.application.ConsultarVehiculos;
import mx.neology.acceso.application.RegistrarVehiculo;
import mx.neology.acceso.application.modelo.ConsultaPagina;
import mx.neology.acceso.application.modelo.VehiculoConTipo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/neo/vehiculos")
@Tag(name = "Vehículos", description = "Altas, listado, detalle e historial de vehículos")
public class VehiculosController {

  private static final Set<String> CAMPOS_ORDEN = Set.of("dentro", "placa", "tipo", "createdAt");

  private final RegistrarVehiculo registrarVehiculo;
  private final ConsultarVehiculos consultarVehiculos;

  public VehiculosController(
      RegistrarVehiculo registrarVehiculo, ConsultarVehiculos consultarVehiculos) {
    this.registrarVehiculo = registrarVehiculo;
    this.consultarVehiculos = consultarVehiculos;
  }

  @PostMapping
  @Operation(summary = "Alta de vehículo con cualquier tipo del catálogo")
  public ResponseEntity<VehiculoResponse> alta(@Valid @RequestBody VehiculoRequest cuerpo) {
    return alta(cuerpo.placa(), cuerpo.tipo());
  }

  @PostMapping("/oficiales")
  @Operation(summary = "Alta de vehículo oficial")
  public ResponseEntity<VehiculoResponse> altaOficial(@Valid @RequestBody PlacaRequest cuerpo) {
    return alta(cuerpo.placa(), "OFICIAL");
  }

  @PostMapping("/residentes")
  @Operation(summary = "Alta de vehículo residente")
  public ResponseEntity<VehiculoResponse> altaResidente(@Valid @RequestBody PlacaRequest cuerpo) {
    return alta(cuerpo.placa(), "RESIDENTE");
  }

  @PostMapping("/no-residentes")
  @Operation(summary = "Alta de vehículo no residente")
  public ResponseEntity<VehiculoResponse> altaNoResidente(@Valid @RequestBody PlacaRequest cuerpo) {
    return alta(cuerpo.placa(), "NO_RESIDENTE");
  }

  @GetMapping
  @Operation(summary = "Listado paginado de vehículos con filtro por placa")
  public PageResponse<VehiculoResumen> listar(
      @RequestParam(required = false) String placa,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "dentro,desc") String sort) {
    ConsultaPagina consulta = ParametrosPagina.de(page, size, sort, CAMPOS_ORDEN);
    return PageResponse.de(consultarVehiculos.listar(placa, consulta), Respuestas::resumen);
  }

  @GetMapping("/{placa}")
  @Operation(summary = "Detalle de un vehículo con su estancia abierta y acumulado del periodo")
  public VehiculoDetalleResponse detalle(@PathVariable String placa) {
    return Respuestas.detalle(consultarVehiculos.detalle(placa));
  }

  @GetMapping("/{placa}/estancias")
  @Operation(summary = "Historial de estancias de un vehículo, las más recientes primero")
  public PageResponse<EstanciaResponse> estancias(
      @PathVariable String placa,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    ConsultaPagina consulta = ParametrosPagina.de(page, size, "entrada,desc", Set.of("entrada"));
    return PageResponse.de(consultarVehiculos.estancias(placa, consulta), Respuestas::estancia);
  }

  private ResponseEntity<VehiculoResponse> alta(String placa, String tipo) {
    VehiculoConTipo vehiculo = registrarVehiculo.ejecutar(placa, tipo);
    return ResponseEntity.created(
            URI.create("/neo/vehiculos/" + vehiculo.vehiculo().placa().valor()))
        .body(Respuestas.vehiculo(vehiculo));
  }
}
