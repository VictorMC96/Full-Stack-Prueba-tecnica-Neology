package com.neology.parking_backend.service.impl;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neology.parking_backend.dto.request.EstanciaEntradaRequest;
import com.neology.parking_backend.dto.request.EstanciaSalidaRequest;
import com.neology.parking_backend.dto.response.EstanciaResponse;
import com.neology.parking_backend.exception.EstanciaNoEncontradaException;
import com.neology.parking_backend.exception.EstanciaYaAbiertaException;
import com.neology.parking_backend.exception.VehiculoNoEncontradoException;
import com.neology.parking_backend.mapper.EstanciaMapper;
import com.neology.parking_backend.model.Estancia;
import com.neology.parking_backend.model.TipoVehiculo;
import com.neology.parking_backend.model.Vehiculo;
import com.neology.parking_backend.repository.EstanciaRepository;
import com.neology.parking_backend.repository.ResidenteRepository;
import com.neology.parking_backend.repository.VehiculoRepository;
import com.neology.parking_backend.service.EstanciaService;
import com.neology.parking_backend.service.TarifaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EstanciaServiceImpl implements EstanciaService {

    private final EstanciaRepository estanciaRepository;
    private final VehiculoRepository vehiculoRepository;
    private final ResidenteRepository residenteRepository;
    private final TarifaService tarifaService;
    private final EstanciaMapper estanciaMapper;

    @Override
    @Transactional
    public EstanciaResponse registrarEntrada(EstanciaEntradaRequest request) {
        String placa = request.getPlaca();

        Vehiculo vehiculo = vehiculoRepository.findById(placa)
                .orElseThrow(() -> new VehiculoNoEncontradoException(placa));

        estanciaRepository.findByVehiculo_PlacaAndFechaSalidaIsNull(placa)
                .ifPresent(estanciaAbierta -> {
                    throw new EstanciaYaAbiertaException(placa);
                });

        Estancia estancia = new Estancia();
        estancia.setVehiculo(vehiculo);

        Estancia guardada = estanciaRepository.save(estancia);
        log.info("Entrada registrada: placa={} estanciaId={}", placa, guardada.getId());
        return estanciaMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public EstanciaResponse registrarSalida(EstanciaSalidaRequest request) {
        String placa = request.getPlaca();

        Estancia estancia = estanciaRepository.findByVehiculo_PlacaAndFechaSalidaIsNull(placa)
                .orElseThrow(() -> new EstanciaNoEncontradaException(placa));

        LocalDateTime fechaSalida = LocalDateTime.now();
        long minutos = Duration.between(estancia.getFechaEntrada(), fechaSalida).toMinutes();

        TipoVehiculo tipo = estancia.getVehiculo().getTipo();
        BigDecimal cobro = tarifaService.calcularCobro(tipo, minutos);

        estancia.setFechaSalida(fechaSalida);
        estancia.setCobro(cobro);
        Estancia guardada = estanciaRepository.save(estancia);

        if (tipo == TipoVehiculo.RESIDENTE) {
            residenteRepository.findById(placa).ifPresent(residente -> {
                residente.setTiempoAcumuladoMinutos(residente.getTiempoAcumuladoMinutos() + minutos);
                residente.setPagoAcumulado(residente.getPagoAcumulado().add(cobro));
                residenteRepository.save(residente);
            });
        }

        log.info("Salida registrada: placa={} minutos={} cobro={}", placa, minutos, cobro);
        return estanciaMapper.toResponse(guardada);
    }

}
