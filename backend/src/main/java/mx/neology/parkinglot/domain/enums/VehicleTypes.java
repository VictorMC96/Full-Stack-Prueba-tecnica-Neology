package mx.neology.parkinglot.domain.enums;

import lombok.Getter;

@Getter
public enum VehicleTypes {
    OFFICIAL("OFFICIAL"),
    RESIDENT("RESIDENT"),
    NO_RESIDENT("NO_RESIDENT"),

    ;

    private final String type;
    VehicleTypes(String type) {
        this.type = type;
    }
}