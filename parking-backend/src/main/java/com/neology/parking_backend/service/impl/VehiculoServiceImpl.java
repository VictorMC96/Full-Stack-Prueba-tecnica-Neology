package com.neology.parking_backend.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.neology.parking_backend.dto.request.AltaVehiculoRequest;
import com.neology.parking_backend.dto.response.EstanciaResponse;
import com.neology.parking_backend.dto.response.VehiculoDetalleResponse;
import com.neology.parking_backend.dto.response.VehiculoResponse;
import com.neology.parking_backend.exception.VehiculoNoEncontradoException;
import com.neology.parking_backend.exception.VehiculoYaExisteException;
import com.neology.parking_backend.mapper.EstanciaMapper;
import com.neology.parking_backend.mapper.VehiculoMapper;
import com.neology.parking_backend.model.Residente;
import com.neology.parking_backend.model.TipoVehiculo;
import com.neology.parking_backend.model.Vehiculo;
import com.neology.parking_backend.repository.EstanciaRepository;
import com.neology.parking_backend.repository.ResidenteRepository;
import com.neology.parking_backend.repository.VehiculoRepository;
import com.neology.parking_backend.service.VehiculoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final ResidenteRepository residenteRepository;
    private final EstanciaRepository estanciaRepository;
    private final VehiculoMapper vehiculoMapper;
    private final EstanciaMapper estanciaMapper;


    @Override
    @Transactional
    public VehiculoResponse altaOficial(AltaVehiculoRequest request) {
        Vehiculo vehiculo = crearVehiculo(request, TipoVehiculo.OFICIAL);
        return vehiculoMapper.toResponse(vehiculo);
    }

    @Override
    @Transactional
    public VehiculoResponse altaNoResidente(AltaVehiculoRequest request) {
        Vehiculo vehiculo = crearVehiculo(request, TipoVehiculo.NO_RESIDENTE);
        return vehiculoMapper.toResponse(vehiculo);
    }

    @Override
    @Transactional
    public VehiculoResponse altaResidente(AltaVehiculoRequest request) {
        Vehiculo vehiculo = crearVehiculo(request, TipoVehiculo.RESIDENTE);

        Residente residente = new Residente();
        residente.setVehiculo(vehiculo);
        residenteRepository.save(residente);

        return vehiculoMapper.toResponse(vehiculo);
    }

    
    private Vehiculo crearVehiculo(AltaVehiculoRequest request, TipoVehiculo tipo) {
        if (vehiculoRepository.existsById(request.getPlaca())) {
            throw new VehiculoYaExisteException(request.getPlaca());
        }
        Vehiculo vehiculo = vehiculoMapper.toEntity(request, tipo);
        Vehiculo guardado = vehiculoRepository.save(vehiculo);
        log.info("Vehículo creado: placa={} tipo={}", guardado.getPlaca(), tipo);
        return guardado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculoResponse> listar() {
        return vehiculoMapper.toResponseList(vehiculoRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculoDetalleResponse obtenerDetalle(String placa) {
        Vehiculo vehiculo = vehiculoRepository.findById(placa)
                .orElseThrow(() -> new VehiculoNoEncontradoException(placa));

        List<EstanciaResponse> estancias = estanciaMapper.toResponseList(
                estanciaRepository.findByVehiculo_PlacaOrderByFechaEntradaDesc(placa));

        Long tiempoAcumuladoMinutos = null;
        BigDecimal pagoAcumulado = null;

        if (vehiculo.getTipo() == TipoVehiculo.RESIDENTE) {
            Residente residente = residenteRepository.findById(placa).orElse(null);
            if (residente != null) {
                tiempoAcumuladoMinutos = residente.getTiempoAcumuladoMinutos();
                pagoAcumulado = residente.getPagoAcumulado();
            }
        }

        return vehiculoMapper.toDetalleResponse(vehiculo, estancias, tiempoAcumuladoMinutos, pagoAcumulado);
    }

}
