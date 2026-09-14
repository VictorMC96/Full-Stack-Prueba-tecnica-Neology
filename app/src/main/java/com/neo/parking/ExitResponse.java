package com.neo.parking;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExitResponse {
    private String plate;
    private double amountToPay;
}