package com.estacionamiento.web;

import com.estacionamiento.servicio.ServicioEstancias;
import com.estacionamiento.web.dto.PlacaRequest;
import com.estacionamiento.web.dto.Respuestas;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/neo/estancias")
@RequiredArgsConstructor
@Tag(name = "Estancias", description = "Registro de entradas y salidas")
public class ControladorEstancias {

    private final ServicioEstancias servicioEstancias;
    private final Mapeador mapeador;

    @PostMapping("/entrada")
    @Operation(summary = "Registrar la entrada de un vehiculo")
    public ResponseEntity<Respuestas.Estancia> entrada(@Valid @RequestBody PlacaRequest peticion) {
        var estancia = servicioEstancias.registrarEntrada(peticion.placa());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapeador.aEstancia(estancia));
    }

    @PostMapping("/salida")
    @Operation(summary = "Registrar la salida de un vehiculo y calcular su cobro")
    public Respuestas.Estancia salida(@Valid @RequestBody PlacaRequest peticion) {
        return mapeador.aEstancia(servicioEstancias.registrarSalida(peticion.placa()));
    }
}
