package edu.unimag.domine.service;

import edu.unimag.domine.api.dto.DoctorScheduleDtos.CreateDoctorScheduleRequest;
import edu.unimag.domine.entities.Doctor;
import edu.unimag.domine.entities.DoctorSchedule;
import edu.unimag.domine.entities.enums.DayOfWeek;
import edu.unimag.domine.exceptions.ConflictException;
import edu.unimag.domine.exceptions.ResourceNotFoundException;
import edu.unimag.domine.exceptions.ValidationException;
import edu.unimag.domine.mappers.DoctorScheduleMapper;
import edu.unimag.domine.repositories.DoctorRepository;
import edu.unimag.domine.repositories.DoctorScheduleRepository;
import edu.unimag.domine.service.implementation.DoctorScheduleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorScheduleServiceImplTest {

    @Mock
    private DoctorScheduleRepository doctorScheduleRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Spy
    private DoctorScheduleMapper mapper = Mappers.getMapper(DoctorScheduleMapper.class);

    @InjectMocks
    private DoctorScheduleServiceImpl service;

    private UUID doctorId = UUID.randomUUID();

    @Test
    void shouldCreateScheduleSuccessfully() {
        var request = new CreateDoctorScheduleRequest(
                doctorId, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0)
        );

        var doctor = Doctor.builder().id(doctorId).build();

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, DayOfWeek.MONDAY))
                .thenReturn(Optional.empty());
        when(doctorScheduleRepository.save(any(DoctorSchedule.class))).thenAnswer(i -> i.getArgument(0));

        var result = service.create(doctorId, request);

        assertThat(result).isNotNull();
        assertThat(result.doctorId()).isEqualTo(doctorId);
        assertThat(result.dayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(result.startsAt()).isEqualTo(LocalTime.of(8, 0));
        assertThat(result.endsAt()).isEqualTo(LocalTime.of(12, 0));
    }

    @Test
    void shouldThrowValidationExceptionWhenStartTimeIsAfterEndTime() {
        var request = new CreateDoctorScheduleRequest(
                doctorId, DayOfWeek.MONDAY, LocalTime.of(14, 0), LocalTime.of(10, 0) // Invalid time
        );

        assertThrows(ValidationException.class, () -> service.create(doctorId, request));
        verify(doctorRepository, never()).findById(any());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenDoctorDoesNotExist() {
        var request = new CreateDoctorScheduleRequest(
                doctorId, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0)
        );

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(doctorId, request));
        verify(doctorScheduleRepository, never()).save(any());
    }

    @Test
    void shouldThrowConflictExceptionWhenScheduleAlreadyExists() {
        var request = new CreateDoctorScheduleRequest(
                doctorId, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0)
        );

        var doctor = Doctor.builder().id(doctorId).build();
        var existingSchedule = DoctorSchedule.builder().build();

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, DayOfWeek.MONDAY))
                .thenReturn(Optional.of(existingSchedule));

        assertThrows(ConflictException.class, () -> service.create(doctorId, request));
        verify(doctorScheduleRepository, never()).save(any());
    }

    @Test
    void shouldGetAllSchedules() {
        var schedule1 = DoctorSchedule.builder()
                .doctor(Doctor.builder().id(doctorId).build())
                .dayOfWeek(DayOfWeek.MONDAY)
                .startsAt(LocalTime.of(8, 0))
                .endsAt(LocalTime.of(12, 0))
                .build();

        when(doctorScheduleRepository.findByDoctorId(doctorId)).thenReturn(List.of(schedule1));

        var result = service.getAllSchedules(doctorId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).dayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
    }
}