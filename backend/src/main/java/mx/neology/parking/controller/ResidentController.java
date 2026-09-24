package mx.neology.parking.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mx.neology.parking.dto.response.ResidentPaymentResponse;
import mx.neology.parking.service.ResidentService;

@RestController
@RequestMapping("/neo/residentes")
@RequiredArgsConstructor
public class ResidentController {

    private final ResidentService residentService;

    @GetMapping("/pagos")
    public List<ResidentPaymentResponse> generatePaymentReport() {

        return residentService.generatePaymentReport();
    }
}