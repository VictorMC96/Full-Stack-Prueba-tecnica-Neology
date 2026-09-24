package mx.neology.parking.service;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mx.neology.parking.enums.VehicleType;

class PaymentServiceTest {

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService();
    }

    @Test
    void shouldReturnZeroForOfficialVehicle() {
        BigDecimal result =
                paymentService.calculateAmount(VehicleType.OFFICIAL, 100);

        assertThat(result)
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldCalculateResidentAmount() {
        BigDecimal result =
                paymentService.calculateAmount(VehicleType.RESIDENT, 100);

        assertThat(result)
                .isEqualByComparingTo(new BigDecimal("5.00"));
    }

    @Test
    void shouldCalculateNonResidentAmount() {
        BigDecimal result =
                paymentService.calculateAmount(VehicleType.NON_RESIDENT, 100);

        assertThat(result)
                .isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    void shouldReturnZeroWhenMinutesAreZero() {
        BigDecimal result =
                paymentService.calculateAmount(VehicleType.NON_RESIDENT, 0);

        assertThat(result)
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldRejectNegativeMinutes() {
        assertThatThrownBy(() ->
                paymentService.calculateAmount(
                        VehicleType.NON_RESIDENT,
                        -10
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Minutes cannot be negative");
    }
}