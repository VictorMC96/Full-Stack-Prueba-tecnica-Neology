package mx.neology.parkinglot.application.usecases;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.vavr.control.Either;
import mx.neology.parkinglot.application.services.PaymentService;
import mx.neology.parkinglot.application.services.StayService;
import mx.neology.parkinglot.domain.dtos.CreatePaymentDto;
import mx.neology.parkinglot.domain.dtos.UpdateStayDto;
import mx.neology.parkinglot.domain.entities.Payment;
import mx.neology.parkinglot.domain.enums.ErrorTypes;
import mx.neology.parkinglot.infrastructure.persistence.repositories.PaymentRepository;

@Service
public class PaymentUseCase implements PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentUseCase.class);

    private final PaymentRepository paymentRepository;
    private final StayService stayService;

    public PaymentUseCase(PaymentRepository paymentRepository, StayService stayService) {
        this.paymentRepository = paymentRepository;
        this.stayService = stayService;
    }

    @Transactional
    @Override
    public Either<ErrorTypes, Payment> create(CreatePaymentDto createPaymentDto) {
        LOGGER.info("$ [INFO]: Executing create payment={}", createPaymentDto);
        LocalDateTime now = LocalDateTime.now();
        String by = "SYSTEM";

        var stay = this.stayService.findById(createPaymentDto.getStayId());
        if (stay.isLeft()) {
            LOGGER.error("$ [ERROR]: Stay not found for payment");
            return Either.left(stay.getLeft());
        }

        Payment payment = Payment.builder()
                .amount(createPaymentDto.getAmount())
                .paymentDate(now)
                .status(createPaymentDto.getStatus() != null ? createPaymentDto.getStatus() : "PAID")
                .stay(stay.get())
                .createdAt(now)
                .createdBy(by)
                .updatedAt(now)
                .updatedBy(by)
                .isActive(true)
                .build();

        var createdPayment = this.paymentRepository.create(payment);
        if (createdPayment.isLeft()) {
            throw new IllegalStateException("Payment creation failed: " + createdPayment.getLeft());
        }

        UpdateStayDto updateStay = UpdateStayDto.builder()
                .exitDate(now)
                .build();

        var updatedStay = this.stayService.update(stay.get().getLicensePlate(), updateStay);
        if (updatedStay.isLeft()) {
            throw new IllegalStateException("Stay update failed: " + updatedStay.getLeft());
        }

        return createdPayment;
    }

    @Override
    public Page<Payment> findAll(int page, int size) {
        LOGGER.info("$ [INFO]: Executing findAll payments page={} size={}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").descending());
        return this.paymentRepository.findAll(pageable);
    }
}