package com.neology.parking_backend.service.impl;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neology.parking_backend.model.Estancia;
import com.neology.parking_backend.repository.EstanciaRepository;
import com.neology.parking_backend.service.MesService;
import com.neology.parking_backend.service.ResidenteService;
import com.neology.parking_backend.service.TarifaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MesServiceImpl implements MesService {

    private final EstanciaRepository estanciaRepository;
    private final ResidenteService residenteService;
    private final TarifaService tarifaService;

    @Override
    @Transactional
    public void iniciarNuevoMes() {
        log.info("Iniciando nuevo mes");
        cerrarEstanciasAbiertas();
        residenteService.reiniciarAcumulados();
    }

    private void cerrarEstanciasAbiertas() {
        List<Estancia> abiertas = estanciaRepository.findByFechaSalidaIsNull();
        LocalDateTime ahora = LocalDateTime.now();

        for (Estancia estancia : abiertas) {
            long minutos = Duration.between(estancia.getFechaEntrada(), ahora).toMinutes();
            BigDecimal cobro = tarifaService.calcularCobro(estancia.getVehiculo().getTipo(), minutos);
            estancia.setFechaSalida(ahora);
            estancia.setCobro(cobro);
        }

        estanciaRepository.saveAll(abiertas);
        log.info("Estancias cerradas al iniciar mes: {}", abiertas.size());
    }

}
