package mx.neology.parking.service;

import mx.neology.parking.entity.Resident;
import mx.neology.parking.repository.ResidentRepository;
import mx.neology.parking.repository.StayRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MonthServiceTest {

    @Mock
    private ResidentRepository residentRepository;

    @Mock
    private StayRepository stayRepository;

    @InjectMocks
    private MonthService monthService;

    @Test
    void shouldResetResidentMinutesAndDeleteStays() {

        Resident residentOne = Resident.builder()
                .id(1L)
                .accumulatedMinutes(100L)
                .build();

        Resident residentTwo = Resident.builder()
                .id(2L)
                .accumulatedMinutes(250L)
                .build();

        List<Resident> residents =
                List.of(residentOne, residentTwo);

        when(residentRepository.findAll())
                .thenReturn(residents);

        monthService.startNewMonth();

        assertThat(residentOne.getAccumulatedMinutes())
                .isZero();

        assertThat(residentTwo.getAccumulatedMinutes())
                .isZero();

        verify(residentRepository).saveAll(residents);
        verify(stayRepository).deleteAll();
    }
}