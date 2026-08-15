package com.neology.parking_backend.service;

import java.util.List;

import com.neology.parking_backend.dto.request.AltaVehiculoRequest;
import com.neology.parking_backend.dto.response.VehiculoDetalleResponse;
import com.neology.parking_backend.dto.response.VehiculoResponse;

public interface VehiculoService {

    VehiculoResponse altaOficial(AltaVehiculoRequest request);

    VehiculoResponse altaResidente(AltaVehiculoRequest request);

    VehiculoResponse altaNoResidente(AltaVehiculoRequest request);

    List<VehiculoResponse> listar();

    VehiculoDetalleResponse obtenerDetalle(String placa);

}
