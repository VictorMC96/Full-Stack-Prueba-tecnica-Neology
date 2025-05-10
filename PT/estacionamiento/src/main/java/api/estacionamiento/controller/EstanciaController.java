package api.estacionamiento.controller;

import api.estacionamiento.models.Estancia;
import api.estacionamiento.service.EstanciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//
import api.estacionamiento.dto.PlacaDTO;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/neo/estancias")
@CrossOrigin(origins = "*")//para poder utilizar el frontend


public class EstanciaController {

    @Autowired
    private EstanciaService estanciaService;

    @PostMapping("/entrada")
    /*
    public void entrada(@RequestBody String placa) {
        estanciaService.registrarEntrada(placa);
    }
     */
    public ResponseEntity<String> registrarEntrada(@RequestBody PlacaDTO dto) {
        Estancia estancia = new Estancia();
        estancia.setPlaca(dto.getPlaca());
        estancia.setFechaHoraEntrada(LocalDateTime.now());
        estanciaService.save(estancia);
        return ResponseEntity.ok("Entrada registrada");
    }

    @PostMapping("/salida")
    public BigDecimal salida(@RequestBody String placa) {
        return estanciaService.registrarSalida(placa);
    }
}