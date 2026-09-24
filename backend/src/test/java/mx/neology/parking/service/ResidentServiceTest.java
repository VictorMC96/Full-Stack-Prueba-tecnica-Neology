package mx.neology.parking.service;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.neology.parking.dto.response.ResidentPaymentResponse;
import mx.neology.parking.entity.Resident;
import mx.neology.parking.entity.Vehicle;
import mx.neology.parking.enums.VehicleType;
import mx.neology.parking.repository.ResidentRepository;

@ExtendWith(MockitoExtension.class)
class ResidentServiceTest {

    @Mock
    private ResidentRepository residentRepository;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private ResidentService residentService;

    @Test
    void shouldGenerateResidentPaymentReport() {

        Vehicle vehicle = Vehicle.builder()
                .id(1L)
                .plate("ABC-123")
                .type(VehicleType.RESIDENT)
                .build();

        Resident resident = Resident.builder()
                .id(1L)
                .vehicle(vehicle)
                .accumulatedMinutes(200L)
                .build();

        when(residentRepository.findAll())
                .thenReturn(List.of(resident));

        when(paymentService.calculateAmount(
                VehicleType.RESIDENT,
                200L
        )).thenReturn(new BigDecimal("10.00"));

        List<ResidentPaymentResponse> report =
                residentService.generatePaymentReport();

        assertThat(report).hasSize(1);

        ResidentPaymentResponse result = report.get(0);

        assertThat(result.plate()).isEqualTo("ABC-123");
        assertThat(result.accumulatedMinutes()).isEqualTo(200L);
        assertThat(result.amount())
                .isEqualByComparingTo(new BigDecimal("10.00"));
    }
}