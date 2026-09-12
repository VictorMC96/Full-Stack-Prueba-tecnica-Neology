package com.estacionamiento.web;

import com.estacionamiento.servicio.ServicioReportes;
import com.estacionamiento.web.dto.Respuestas;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//Controlador-Residentes
@RestController
@RequestMapping("/neo/residentes")
@RequiredArgsConstructor
@Tag(name = "Residentes", description = "Informe de pagos")
public class ControladorResidentes {

    private final ServicioReportes servicioReportes;
    private final Mapeador mapeador;

    //Informe-Pagos
    @GetMapping("/pagos")
    @Operation(summary = "Generar el informe de pagos de residentes del periodo actual")
    public Respuestas.InformePagos pagos() {
        return mapeador.aInforme(servicioReportes.informePagos());
    }
}
