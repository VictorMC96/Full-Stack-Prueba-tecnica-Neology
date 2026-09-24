package mx.neology.parking.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.neology.parking.entity.Resident;
import mx.neology.parking.entity.Stay;
import mx.neology.parking.entity.Vehicle;
import mx.neology.parking.enums.VehicleType;
import mx.neology.parking.exception.OpenStayNotFoundException;
import mx.neology.parking.exception.StayAlreadyOpenException;
import mx.neology.parking.repository.ResidentRepository;
import mx.neology.parking.repository.StayRepository;

@ExtendWith(MockitoExtension.class)
class StayServiceTest {

    @Mock
    private StayRepository stayRepository;

    @Mock
    private ResidentRepository residentRepository;

    @Mock
    private VehicleService vehicleService;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private StayService stayService;

    @Test
    void shouldRegisterEntry() {

        Vehicle vehicle = Vehicle.builder()
                .id(1L)
                .plate("ABC-123")
                .type(VehicleType.NON_RESIDENT)
                .build();

        when(vehicleService.findOrCreateNonResident("ABC-123"))
                .thenReturn(vehicle);

        when(stayRepository.existsByVehicleAndExitDateTimeIsNull(vehicle))
                .thenReturn(false);

        when(stayRepository.save(any(Stay.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Stay result = stayService.registerEntry("ABC-123");

        assertThat(result.getVehicle()).isEqualTo(vehicle);
        assertThat(result.getEntryDateTime()).isNotNull();
        assertThat(result.getExitDateTime()).isNull();
        assertThat(result.getDurationMinutes()).isNull();
        assertThat(result.getAmount()).isNull();

        verify(stayRepository).save(any(Stay.class));
    }

    @Test
    void shouldRejectEntryWhenVehicleAlreadyHasOpenStay() {

        Vehicle vehicle = Vehicle.builder()
                .id(1L)
                .plate("ABC-123")
                .type(VehicleType.NON_RESIDENT)
                .build();

        when(vehicleService.findOrCreateNonResident("ABC-123"))
                .thenReturn(vehicle);

        when(stayRepository.existsByVehicleAndExitDateTimeIsNull(vehicle))
                .thenReturn(true);

        assertThatThrownBy(() ->
                stayService.registerEntry("ABC-123")
        )
                .isInstanceOf(StayAlreadyOpenException.class)
                .hasMessageContaining("ABC-123");

        verify(stayRepository, never()).save(any(Stay.class));
    }

    @Test
    void shouldRegisterExitAndCalculatePayment() {

        Vehicle vehicle = Vehicle.builder()
                .id(1L)
                .plate("ABC-123")
                .type(VehicleType.NON_RESIDENT)
                .build();

        Stay stay = Stay.builder()
                .id(1L)
                .vehicle(vehicle)
                .entryDateTime(LocalDateTime.now().minusMinutes(30))
                .build();

        when(vehicleService.findByPlate("ABC-123"))
                .thenReturn(vehicle);

        when(stayRepository
                .findFirstByVehicleAndExitDateTimeIsNullOrderByEntryDateTimeDesc(vehicle))
                .thenReturn(Optional.of(stay));

        when(paymentService.calculateAmount(
                eq(VehicleType.NON_RESIDENT),
                anyLong()
        )).thenReturn(new BigDecimal("15.00"));

        when(stayRepository.save(any(Stay.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Stay result = stayService.registerExit("ABC-123");

        assertThat(result.getExitDateTime()).isNotNull();
        assertThat(result.getDurationMinutes()).isNotNull();
        assertThat(result.getDurationMinutes()).isGreaterThanOrEqualTo(30);
        assertThat(result.getAmount())
                .isEqualByComparingTo(new BigDecimal("15.00"));

        verify(paymentService).calculateAmount(
                eq(VehicleType.NON_RESIDENT),
                anyLong()
        );

        verify(stayRepository).save(stay);
    }

    @Test
    void shouldRejectExitWhenThereIsNoOpenStay() {

        Vehicle vehicle = Vehicle.builder()
                .id(1L)
                .plate("ABC-123")
                .type(VehicleType.NON_RESIDENT)
                .build();

        when(vehicleService.findByPlate("ABC-123"))
                .thenReturn(vehicle);

        when(stayRepository
                .findFirstByVehicleAndExitDateTimeIsNullOrderByEntryDateTimeDesc(vehicle))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                stayService.registerExit("ABC-123")
        )
                .isInstanceOf(OpenStayNotFoundException.class)
                .hasMessageContaining("ABC-123");

        verify(paymentService, never())
                .calculateAmount(any(), anyLong());

        verify(stayRepository, never())
                .save(any(Stay.class));
    }

    @Test
    void shouldAccumulateMinutesForResidentWhenRegisteringExit() {

        Vehicle vehicle = Vehicle.builder()
                .id(1L)
                .plate("RES-123")
                .type(VehicleType.RESIDENT)
                .build();

        Resident resident = Resident.builder()
                .id(1L)
                .vehicle(vehicle)
                .accumulatedMinutes(100L)
                .build();

        Stay stay = Stay.builder()
                .id(1L)
                .vehicle(vehicle)
                .entryDateTime(LocalDateTime.now().minusMinutes(20))
                .build();

        when(vehicleService.findByPlate("RES-123"))
                .thenReturn(vehicle);

        when(stayRepository
                .findFirstByVehicleAndExitDateTimeIsNullOrderByEntryDateTimeDesc(vehicle))
                .thenReturn(Optional.of(stay));

        when(paymentService.calculateAmount(
                eq(VehicleType.RESIDENT),
                anyLong()
        )).thenReturn(new BigDecimal("1.00"));

        when(residentRepository.findByVehicle(vehicle))
                .thenReturn(Optional.of(resident));

        when(stayRepository.save(any(Stay.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Long> minutesCaptor =
                ArgumentCaptor.forClass(Long.class);

        stayService.registerExit("RES-123");

        verify(paymentService).calculateAmount(
                eq(VehicleType.RESIDENT),
                minutesCaptor.capture()
        );

        long stayMinutes = minutesCaptor.getValue();

        assertThat(resident.getAccumulatedMinutes())
                .isEqualTo(100L + stayMinutes);

        verify(residentRepository).save(resident);
    }
}