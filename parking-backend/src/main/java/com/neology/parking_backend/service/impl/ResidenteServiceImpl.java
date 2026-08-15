package com.neology.parking_backend.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neology.parking_backend.dto.response.ResidentePagoResponse;
import com.neology.parking_backend.mapper.ResidenteMapper;
import com.neology.parking_backend.model.Residente;
import com.neology.parking_backend.repository.ResidenteRepository;
import com.neology.parking_backend.service.ResidenteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResidenteServiceImpl implements ResidenteService {

    private final ResidenteRepository residenteRepository;
    private final ResidenteMapper residenteMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ResidentePagoResponse> generarInformePagos() {
        return residenteMapper.toPagoResponseList(residenteRepository.findAll());
    }

    @Override
    @Transactional
    public void reiniciarAcumulados() {
        List<Residente> residentes = residenteRepository.findAll();
        residentes.forEach(residente -> {
            residente.setTiempoAcumuladoMinutos(0L);
            residente.setPagoAcumulado(BigDecimal.ZERO);
        });
        residenteRepository.saveAll(residentes);
        log.info("Acumulados de residentes reiniciados: total={}", residentes.size());
    }

}
