package com.neo.parking;

import jakarta.validation.constraints.NotEmpty;

public class VehicleRequest {
    @NotEmpty 
    public  String plate;

    Vehicle toResident(){
        return new Vehicle(null, this.plate, VehicleType.RESIDENT);
    }
    
    Vehicle toNoResident(){
        return new Vehicle(null, this.plate, VehicleType.NO_RESIDENT);
    }

    Vehicle toOfficial(){
        return new Vehicle(null, this.plate, VehicleType.OFFICIALS);
    }
}
