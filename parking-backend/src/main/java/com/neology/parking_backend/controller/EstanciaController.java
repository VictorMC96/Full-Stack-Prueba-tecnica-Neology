package com.neology.parking_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.neology.parking_backend.dto.request.EstanciaEntradaRequest;
import com.neology.parking_backend.dto.request.EstanciaSalidaRequest;
import com.neology.parking_backend.dto.response.EstanciaResponse;
import com.neology.parking_backend.exception.ApiErrorResponse;
import com.neology.parking_backend.service.EstanciaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/estancias")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Estancias", description = "Registro de entrada y salida de vehículos del estacionamiento")
public class EstanciaController {

    private final EstanciaService estanciaService;

    @PostMapping("/entrada")
    @ResponseStatus(code = HttpStatus.CREATED)
    @Operation(summary = "Registrar entrada", description = "Abre una nueva estancia para el vehículo. Falla si ya tiene una estancia abierta o si el vehículo no existe.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Entrada registrada"),
            @ApiResponse(responseCode = "404", description = "El vehículo no existe",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "El vehículo ya tiene una estancia abierta",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public EstanciaResponse registrarEntrada(@Valid @RequestBody EstanciaEntradaRequest request) {
        return estanciaService.registrarEntrada(request);
    }

    @PostMapping("/salida")
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "Registrar salida", description = "Cierra la estancia abierta del vehículo, calcula el cobro y, si es residente, acumula tiempo y pago.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Salida registrada y cobro calculado"),
            @ApiResponse(responseCode = "404", description = "No hay una estancia abierta para esa placa",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public EstanciaResponse registrarSalida(@Valid @RequestBody EstanciaSalidaRequest request) {
        return estanciaService.registrarSalida(request);
    }

}
