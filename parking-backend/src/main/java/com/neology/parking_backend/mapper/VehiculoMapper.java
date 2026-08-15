package com.neology.parking_backend.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.neology.parking_backend.dto.request.AltaVehiculoRequest;
import com.neology.parking_backend.dto.response.EstanciaResponse;
import com.neology.parking_backend.dto.response.VehiculoDetalleResponse;
import com.neology.parking_backend.dto.response.VehiculoResponse;
import com.neology.parking_backend.model.TipoVehiculo;
import com.neology.parking_backend.model.Vehiculo;

@Mapper(componentModel = "spring")
public interface VehiculoMapper {

    @Mapping(target = "placa", source = "request.placa")
    @Mapping(target = "tipo", source = "tipo")
    Vehiculo toEntity(AltaVehiculoRequest request, TipoVehiculo tipo);

    VehiculoResponse toResponse(Vehiculo vehiculo);

    List<VehiculoResponse> toResponseList(List<Vehiculo> vehiculos);

    @Mapping(target = "placa", source = "vehiculo.placa")
    @Mapping(target = "tipo", source = "vehiculo.tipo")
    @Mapping(target = "estancias", source = "estancias")
    @Mapping(target = "tiempoAcumuladoMinutos", source = "tiempoAcumuladoMinutos")
    @Mapping(target = "pagoAcumulado", source = "pagoAcumulado")
    VehiculoDetalleResponse toDetalleResponse(Vehiculo vehiculo, List<EstanciaResponse> estancias,
            Long tiempoAcumuladoMinutos, BigDecimal pagoAcumulado);

}
