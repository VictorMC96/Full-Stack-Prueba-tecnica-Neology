package com.neology.parking_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.neology.parking_backend.dto.request.AltaVehiculoRequest;
import com.neology.parking_backend.dto.response.VehiculoDetalleResponse;
import com.neology.parking_backend.dto.response.VehiculoResponse;
import com.neology.parking_backend.exception.ApiErrorResponse;
import com.neology.parking_backend.service.VehiculoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping(path = "/vehiculos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Vehículos", description = "Alta y consulta de vehículos oficiales, residentes y no residentes")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    @PostMapping("/oficiales")
    @ResponseStatus(code = HttpStatus.CREATED)
    @Operation(summary = "Alta de vehículo oficial", description = "Registra un vehículo oficial. No paga estacionamiento.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vehículo creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe un vehículo con esa placa",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public VehiculoResponse altaVehiculosOfiiales(@RequestBody @Valid AltaVehiculoRequest entity) {
        return vehiculoService.altaOficial(entity);
    }

    @PostMapping("/residentes")
    @ResponseStatus(code = HttpStatus.CREATED)
    @Operation(summary = "Alta de vehículo residente", description = "Registra un vehículo residente. Paga $0.05/minuto acumulado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vehículo creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe un vehículo con esa placa",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public VehiculoResponse altaVehiculosResidentes(@RequestBody @Valid AltaVehiculoRequest entity) {
        return vehiculoService.altaResidente(entity);
    }

    @PostMapping("/no-residentes")
    @ResponseStatus(code = HttpStatus.CREATED)
    @Operation(summary = "Alta de vehículo no residente", description = "Registra un vehículo no residente. Paga $0.5/minuto al salir.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vehículo creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe un vehículo con esa placa",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public VehiculoResponse altaVehiculosNoResidentes(@RequestBody @Valid AltaVehiculoRequest entity) {
        return vehiculoService.altaNoResidente(entity);
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "Listar vehículos", description = "Devuelve todos los vehículos registrados.")
    public List<VehiculoResponse> listar() {
        return vehiculoService.listar();
    }

    @GetMapping("/{placa}")
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "Detalle de un vehículo", description = "Devuelve el vehículo, su historial de estancias y, si es residente, su tiempo y pago acumulado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalle encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un vehículo con esa placa",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public VehiculoDetalleResponse detalle(@PathVariable String placa) {
        return vehiculoService.obtenerDetalle(placa);
    }

}
