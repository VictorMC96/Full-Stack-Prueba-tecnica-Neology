package mx.neology.acceso.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import mx.neology.acceso.application.ActualizarTipoVehiculo;
import mx.neology.acceso.application.ConsultarTiposVehiculo;
import mx.neology.acceso.application.EliminarTipoVehiculo;
import mx.neology.acceso.application.RegistrarTipoVehiculo;
import mx.neology.acceso.domain.TipoVehiculo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/neo/tipos-vehiculo")
@Tag(
    name = "Tipos de vehículo",
    description = "Catálogo administrable de tipos de vehículo, su modelo de cobro y su tarifa")
public class TiposVehiculoController {

  private final ConsultarTiposVehiculo consultar;
  private final RegistrarTipoVehiculo registrar;
  private final ActualizarTipoVehiculo actualizar;
  private final EliminarTipoVehiculo eliminar;

  public TiposVehiculoController(
      ConsultarTiposVehiculo consultar,
      RegistrarTipoVehiculo registrar,
      ActualizarTipoVehiculo actualizar,
      EliminarTipoVehiculo eliminar) {
    this.consultar = consultar;
    this.registrar = registrar;
    this.actualizar = actualizar;
    this.eliminar = eliminar;
  }

  @GetMapping
  @Operation(summary = "Listar los tipos de vehículo ordenados por código")
  public List<TipoVehiculoResponse> listar() {
    return consultar.listar().stream().map(TipoVehiculoResponse::de).toList();
  }

  @GetMapping("/{codigo}")
  @Operation(summary = "Consultar un tipo de vehículo")
  public TipoVehiculoResponse detalle(@PathVariable String codigo) {
    return TipoVehiculoResponse.de(consultar.buscar(codigo));
  }

  @PostMapping
  @Operation(summary = "Crear un tipo de vehículo con su modelo de cobro y tarifa")
  public ResponseEntity<TipoVehiculoResponse> crear(
      @Valid @RequestBody TipoVehiculoRequest cuerpo) {
    TipoVehiculo creado =
        registrar.ejecutar(
            cuerpo.codigo(), cuerpo.nombre(), cuerpo.modeloCobro(), cuerpo.tarifaPorMinuto());
    return ResponseEntity.created(URI.create("/neo/tipos-vehiculo/" + creado.codigo().valor()))
        .body(TipoVehiculoResponse.de(creado, 0));
  }

  @PutMapping("/{codigo}")
  @Operation(summary = "Actualizar nombre, modelo de cobro y tarifa de un tipo de vehículo")
  public TipoVehiculoResponse modificar(
      @PathVariable String codigo, @Valid @RequestBody TipoVehiculoActualizacionRequest cuerpo) {
    return TipoVehiculoResponse.de(
        actualizar.ejecutar(
            codigo, cuerpo.nombre(), cuerpo.modeloCobro(), cuerpo.tarifaPorMinuto()));
  }

  @DeleteMapping("/{codigo}")
  @Operation(summary = "Eliminar un tipo de vehículo sin vehículos registrados")
  public ResponseEntity<Void> borrar(@PathVariable String codigo) {
    eliminar.ejecutar(codigo);
    return ResponseEntity.noContent().build();
  }
}
