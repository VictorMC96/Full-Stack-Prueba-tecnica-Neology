package mx.neology.parkinglot.infrastructure.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.neology.parkinglot.application.services.PaymentService;
import mx.neology.parkinglot.domain.dtos.CreatePaymentDto;
import mx.neology.parkinglot.domain.dtos.DetailPaymentDto;
import mx.neology.parkinglot.domain.entities.Payment;
import mx.neology.parkinglot.domain.enums.ResponseCodes;
import mx.neology.parkinglot.domain.models.ResponseApp;
import mx.neology.parkinglot.domain.models.ResponseMapper;

@Tag(name = "Payments", description = "Registro y consulta de pagos")
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Registrar un pago")
    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseApp<DetailPaymentDto>> create(@Valid @RequestBody CreatePaymentDto createPaymentDto) {
        LOGGER.info("$ [INFO]: Executing create payment={}", createPaymentDto);
        return this.paymentService.create(createPaymentDto)
            .map(Payment::toDto)
            .map(payment -> ResponseMapper.toResponse(ResponseCodes.CREATED, payment))
            .getOrElseGet(errorCode -> ResponseMapper.toError(errorCode));
    }

    @Operation(summary = "Listar pagos paginados")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseApp<Page<DetailPaymentDto>>> getAll(
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        var paymentsPage = this.paymentService.findAll(page, size)
            .map(Payment::toDto);
        return ResponseMapper.toResponse(ResponseCodes.GET_ALL, paymentsPage);
    }
}