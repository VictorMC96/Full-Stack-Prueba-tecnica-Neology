package mx.neology.parkinglot.infrastructure.persistence.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.vavr.control.Either;
import mx.neology.parkinglot.domain.entities.Payment;
import mx.neology.parkinglot.domain.enums.ErrorTypes;
import mx.neology.parkinglot.infrastructure.persistence.jpa.PaymentJpaRepository;
import mx.neology.parkinglot.infrastructure.persistence.repositories.PaymentRepository;

@Repository
public class PaymentDao implements PaymentRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentDao.class);

    private final PaymentJpaRepository paymentJpaRepository;

    public PaymentDao(PaymentJpaRepository paymentJpaRepository) {
        this.paymentJpaRepository = paymentJpaRepository;
    }

    @Transactional
    @Override
    public Either<ErrorTypes, Payment> create(Payment payment) {
        try {
            return Either.right(this.paymentJpaRepository.saveAndFlush(payment));
        } catch (Exception e) {
            LOGGER.error("$ [ERROR]: Error to insert payment={}", e.getMessage(), e);
            return Either.left(ErrorTypes.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Page<Payment> findAll(Pageable pageable) {
        LOGGER.info("$ [INFO]: Executing findAll payments jpa method...");
        return this.paymentJpaRepository.findAll(pageable);
    }
}