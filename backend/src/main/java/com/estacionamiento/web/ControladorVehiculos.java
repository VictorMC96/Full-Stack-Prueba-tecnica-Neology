package com.estacionamiento.web;

import com.estacionamiento.dominio.TipoVehiculo;
import com.estacionamiento.dominio.Vehiculo;
import com.estacionamiento.error.SolicitudInvalida;
import com.estacionamiento.servicio.ServicioEstancias;
import com.estacionamiento.servicio.ServicioVehiculos;
import com.estacionamiento.web.dto.AltaVehiculoRequest;
import com.estacionamiento.web.dto.Respuestas;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/neo/vehiculos")
@RequiredArgsConstructor
@Tag(name = "Vehiculos", description = "Alta y consulta de vehiculos")
public class ControladorVehiculos {

    private static final Set<String> ORDENES_PERMITIDOS = Set.of("placa", "tipo", "propietario", "alta");

    private final ServicioVehiculos servicioVehiculos;
    private final ServicioEstancias servicioEstancias;
    private final Mapeador mapeador;

    @PostMapping("/oficiales")
    @Operation(summary = "Alta de vehiculo oficial")
    public ResponseEntity<Respuestas.Vehiculo> altaOficial(@Valid @RequestBody AltaVehiculoRequest peticion) {
        return crear(peticion, TipoVehiculo.OFICIAL);
    }

    @PostMapping("/residentes")
    @Operation(summary = "Alta de vehiculo residente")
    public ResponseEntity<Respuestas.Vehiculo> altaResidente(@Valid @RequestBody AltaVehiculoRequest peticion) {
        return crear(peticion, TipoVehiculo.RESIDENTE);
    }

    @PostMapping("/no-residentes")
    @Operation(summary = "Alta de vehiculo no residente")
    public ResponseEntity<Respuestas.Vehiculo> altaNoResidente(@Valid @RequestBody AltaVehiculoRequest peticion) {
        return crear(peticion, TipoVehiculo.NO_RESIDENTE);
    }

    @GetMapping
    @Operation(summary = "Listado de vehiculos con filtro, paginacion y orden")
    public Respuestas.Pagina<Respuestas.Vehiculo> listar(
            @RequestParam(defaultValue = "") String placa,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "placa") String sort,
            @RequestParam(defaultValue = "asc") String direccion) {

        Sort orden = Sort.by(direccionValida(direccion), campoValido(sort));
        Page<Vehiculo> pagina = servicioVehiculos.listar(placa, PageRequest.of(page, size, orden));
        List<Respuestas.Vehiculo> contenido = pagina.getContent().stream().map(mapeador::aVehiculo).toList();

        return new Respuestas.Pagina<>(
                contenido, pagina.getNumber(), pagina.getSize(), pagina.getTotalElements(), pagina.getTotalPages());
    }

    @GetMapping("/{placa}")
    @Operation(summary = "Detalle de un vehiculo con su historial de estancias")
    public Respuestas.DetalleVehiculo detalle(@PathVariable String placa) {
        Vehiculo vehiculo = servicioVehiculos.buscar(placa);
        List<Respuestas.Estancia> estancias = servicioEstancias.historial(vehiculo).stream()
                .map(mapeador::aEstancia).toList();
        return new Respuestas.DetalleVehiculo(mapeador.aVehiculo(vehiculo), estancias);
    }

    private String campoValido(String campo) {
        if (!ORDENES_PERMITIDOS.contains(campo)) {
            throw new SolicitudInvalida("Solo se puede ordenar por " + String.join(", ", ORDENES_PERMITIDOS));
        }
        return campo;
    }

    private Sort.Direction direccionValida(String direccion) {
        return "desc".equalsIgnoreCase(direccion) ? Sort.Direction.DESC : Sort.Direction.ASC;
    }

    private ResponseEntity<Respuestas.Vehiculo> crear(AltaVehiculoRequest peticion, TipoVehiculo tipo) {
        Vehiculo vehiculo = servicioVehiculos.alta(peticion.placa(), tipo, peticion.propietario());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapeador.aVehiculo(vehiculo));
    }
}
