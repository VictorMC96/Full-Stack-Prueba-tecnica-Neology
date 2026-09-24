package mx.neology.parking.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mx.neology.parking.dto.response.MessageResponse;
import mx.neology.parking.service.MonthService;

@RestController
@RequestMapping("/neo/mes")
@RequiredArgsConstructor
public class MonthController {

    private final MonthService monthService;

    @PostMapping("/iniciar")
    public MessageResponse startNewMonth() {

        monthService.startNewMonth();

        return new MessageResponse(
                "New month started successfully"
        );
    }
}