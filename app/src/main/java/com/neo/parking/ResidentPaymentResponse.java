package com.neo.parking;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data 
@AllArgsConstructor 
class ResidentPaymentResponse {
    private String plate;
    private int totalMinutes;
    private double amountToPay;
}