package com.neology.parking_backend.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.neology.parking_backend.dto.response.ResidentePagoResponse;
import com.neology.parking_backend.model.Residente;

@Mapper(componentModel = "spring")
public interface ResidenteMapper {

    ResidentePagoResponse toPagoResponse(Residente residente);

    List<ResidentePagoResponse> toPagoResponseList(List<Residente> residentes);

}
