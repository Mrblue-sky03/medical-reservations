package edu.unimag.domine.service;

import edu.unimag.domine.entities.Appointment;
import edu.unimag.domine.entities.DoctorSchedule;

import edu.unimag.domine.entities.enums.DayOfWeek;
import edu.unimag.domine.entities.enums.Status;
import edu.unimag.domine.exceptions.ResourceNotFoundException;
import edu.unimag.domine.exceptions.ValidationException;
import edu.unimag.domine.repositories.AppointmentRepository;
import edu.unimag.domine.repositories.AppointmentTypeRepository;
import edu.unimag.domine.repositories.DoctorRepository;
import edu.unimag.domine.repositories.DoctorScheduleRepository;
import edu.unimag.domine.service.implementation.AvailabilityServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorScheduleRepository doctorScheduleRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentTypeRepository appointmentTypeRepository;

    @InjectMocks
    private AvailabilityServiceImpl service;

    private final UUID doctorId = UUID.randomUUID();
    private final UUID officeId = UUID.randomUUID();
    private final LocalDate targetDate = LocalDate.of(2023, 10, 16); // Lunes

    @Test
    void shouldReturnAvailableSlotsSuccessfully() {
        var schedule = DoctorSchedule.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startsAt(LocalTime.of(8, 0))
                .endsAt(LocalTime.of(10, 0))
                .build();

        var doctorAppointment = Appointment.builder()
                .startAt(LocalTime.of(8, 30))
                .endAt(LocalTime.of(9, 0))
                .status(Status.SCHEDULED)
                .build();

        var officeAppointment = Appointment.builder()
                .startAt(LocalTime.of(9, 30))
                .endAt(LocalTime.of(10, 0))
                .status(Status.SCHEDULED)
                .build();

        when(doctorRepository.existsById(doctorId)).thenReturn(true);
        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, DayOfWeek.MONDAY))
                .thenReturn(Optional.of(schedule));

        when(appointmentRepository.findByDoctorIdAndDate(doctorId, targetDate))
                .thenReturn(List.of(doctorAppointment));
        when(appointmentRepository.findByOfficeIdAndDateAndStartAtBetween(eq(officeId), eq(targetDate), any(), any()))
                .thenReturn(List.of(officeAppointment));

        var result = service.getAvailableSlots(doctorId, officeId, targetDate, null);

        assertThat(result).hasSize(2);

        assertThat(result.get(0).startsAt()).isEqualTo(LocalTime.of(8, 0));
        assertThat(result.get(0).endsAt()).isEqualTo(LocalTime.of(8, 30));

        assertThat(result.get(1).startsAt()).isEqualTo(LocalTime.of(9, 0));
        assertThat(result.get(1).endsAt()).isEqualTo(LocalTime.of(9, 30));
    }

    @Test
    void shouldIgnoreCancelledAppointments() {
        var schedule = DoctorSchedule.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startsAt(LocalTime.of(8, 0))
                .endsAt(LocalTime.of(9, 0))
                .build();

        var cancelledAppointment = Appointment.builder()
                .startAt(LocalTime.of(8, 0))
                .endAt(LocalTime.of(8, 30))
                .status(Status.CANCELLED)
                .build();

        when(doctorRepository.existsById(doctorId)).thenReturn(true);
        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, DayOfWeek.MONDAY))
                .thenReturn(Optional.of(schedule));
        when(appointmentRepository.findByDoctorIdAndDate(doctorId, targetDate))
                .thenReturn(List.of(cancelledAppointment));
        when(appointmentRepository.findByOfficeIdAndDateAndStartAtBetween(eq(officeId), eq(targetDate), any(), any()))
                .thenReturn(List.of());

        var result = service.getAvailableSlots(doctorId, officeId, targetDate, null);

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldThrowValidationExceptionWhenDoctorIdIsNull() {
        assertThrows(ValidationException.class,
                () -> service.getAvailableSlots(null, officeId, targetDate, null));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenDoctorDoesNotExist() {
        when(doctorRepository.existsById(doctorId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> service.getAvailableSlots(doctorId, officeId, targetDate, null));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenNoSchedule() {
        when(doctorRepository.existsById(doctorId)).thenReturn(true);
        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, DayOfWeek.MONDAY))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getAvailableSlots(doctorId, officeId, targetDate, null));
    }
}