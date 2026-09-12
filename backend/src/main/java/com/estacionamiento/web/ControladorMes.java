package com.estacionamiento.web;

import com.estacionamiento.servicio.ServicioMes;
import com.estacionamiento.web.dto.Respuestas;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//Controlador-Mes
@RestController
@RequestMapping("/neo/mes")
@RequiredArgsConstructor
@Tag(name = "Mes", description = "Cierre de periodo")
public class ControladorMes {

    private final ServicioMes servicioMes;

    //Inicio-Mes
    @PostMapping("/iniciar")
    @Operation(summary = "Reiniciar el mes: pone en cero los acumulados de residentes")
    public Respuestas.Mes iniciar() {
        int periodo = servicioMes.iniciar();
        return new Respuestas.Mes(periodo, "Se inicio el periodo " + periodo);
    }
}
