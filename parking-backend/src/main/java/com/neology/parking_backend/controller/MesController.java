package com.neology.parking_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.neology.parking_backend.service.MesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/mes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Mes", description = "Cierre y reinicio del ciclo mensual de cobros")
public class MesController {

    private final MesService mesService;

    @PostMapping("/iniciar")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @Operation(summary = "Iniciar nuevo mes", description = "Cierra las estancias que sigan abiertas (calculando su cobro) y resetea a cero el tiempo y pago acumulado de todos los residentes.")
    public void iniciarMes() {
        mesService.iniciarNuevoMes();
    }

}
