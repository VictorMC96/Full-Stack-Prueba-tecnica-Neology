package com.neology.parking_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.neology.parking_backend.dto.response.ResidentePagoResponse;
import com.neology.parking_backend.service.ResidenteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/residentes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Residentes", description = "Reportes de pagos acumulados de vehículos residentes")
public class ResidenteController {

    private final ResidenteService residenteService;

    @GetMapping("/pagos")
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "Informe de pagos", description = "Devuelve el tiempo y pago acumulado de cada residente en el mes actual.")
    public List<ResidentePagoResponse> informePagos() {
        return residenteService.generarInformePagos();
    }

}
