package com.neology.parking_backend.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.neology.parking_backend.dto.response.EstanciaResponse;
import com.neology.parking_backend.model.Estancia;

@Mapper(componentModel = "spring")
public interface EstanciaMapper {

    @Mapping(target = "placa", source = "vehiculo.placa")
    EstanciaResponse toResponse(Estancia estancia);

    List<EstanciaResponse> toResponseList(List<Estancia> estancias);

}
