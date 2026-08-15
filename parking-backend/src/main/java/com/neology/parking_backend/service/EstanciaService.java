package com.neology.parking_backend.service;

import com.neology.parking_backend.dto.request.EstanciaEntradaRequest;
import com.neology.parking_backend.dto.request.EstanciaSalidaRequest;
import com.neology.parking_backend.dto.response.EstanciaResponse;

public interface EstanciaService {

    EstanciaResponse registrarEntrada(EstanciaEntradaRequest request);

    EstanciaResponse registrarSalida(EstanciaSalidaRequest request);

}
