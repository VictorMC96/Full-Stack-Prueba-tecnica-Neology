package com.neology.parking_backend.service;

import java.util.List;

import com.neology.parking_backend.dto.response.ResidentePagoResponse;

public interface ResidenteService {

    List<ResidentePagoResponse> generarInformePagos();

    void reiniciarAcumulados();

}
