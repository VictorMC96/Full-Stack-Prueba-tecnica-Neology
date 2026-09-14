package com.neo.parking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

public class ParkingStayControllerTest {
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private StayRepository stayRepository;
    @Mock
    private ResidentRepository residentRepository;

    @InjectMocks
    private ParkingStayController stayController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void exitNonResident_ShouldCalculatePayment() {
        VehicleRequest req = new VehicleRequest();
        req.plate = "XYZ-123";

        Vehicle noResidentVehicle = new Vehicle(UUID.randomUUID(), "XYZ-123", VehicleType.NO_RESIDENT);
        Stay openStay = new Stay(UUID.randomUUID(), "XYZ-123", LocalDateTime.now().minusMinutes(60), null);

        when(stayRepository.findByPlateAndExitDateIsNull(eq("XYZ-123"), any(Sort.class)))
                .thenReturn(Optional.of(openStay));
        when(vehicleRepository.findByPlate("XYZ-123"))
                .thenReturn(Optional.of(noResidentVehicle));

        ExitResponse response = stayController.createExit(req);

        assertEquals(30.0, response.getAmountToPay());
        verify(stayRepository, times(1)).save(any(Stay.class));
    }

    @Test
    void exitResident_ShouldAccumulateTimeAndReturnZeroPayment() {
        VehicleRequest req = new VehicleRequest();
        req.plate = "RES-999";

        Vehicle residentVehicle = new Vehicle(UUID.randomUUID(), "RES-999", VehicleType.RESIDENT);
        Stay openStay = new Stay(UUID.randomUUID(), "RES-999", LocalDateTime.now().minusMinutes(120), null);
        Resident resident = new Resident(UUID.randomUUID(), "RES-999", 10);

        when(stayRepository.findByPlateAndExitDateIsNull(eq("RES-999"), any(Sort.class)))
                .thenReturn(Optional.of(openStay));
        when(vehicleRepository.findByPlate("RES-999"))
                .thenReturn(Optional.of(residentVehicle));
        when(residentRepository.findByPlate("RES-999"))
                .thenReturn(Optional.of(resident));

        ExitResponse response = stayController.createExit(req);

        assertEquals(0.0, response.getAmountToPay());
        assertEquals(130, resident.getCumulativeTime());
        verify(residentRepository, times(1)).save(resident);
    }
}
