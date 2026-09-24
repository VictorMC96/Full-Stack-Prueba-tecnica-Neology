package mx.neology.parking.dto.response;

import mx.neology.parking.enums.VehicleType;

public record VehicleResponse(
        Long id,
        String plate,
        VehicleType type
) {
}