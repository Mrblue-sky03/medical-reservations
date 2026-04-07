package edu.unimag.domine.service;

import edu.unimag.domine.api.dto.AppointmentDtos.*;
import edu.unimag.domine.entities.*;
import edu.unimag.domine.entities.enums.Status;
import edu.unimag.domine.exceptions.BusinessException;
import edu.unimag.domine.exceptions.ConflictException;
import edu.unimag.domine.exceptions.ResourceNotFoundException;
import edu.unimag.domine.mappers.AppointmentMapper;
import edu.unimag.domine.repositories.*;
import edu.unimag.domine.service.implementation.AppointmentServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private OfficeRepository officeRepository;
    @Mock private AppointmentTypeRepository appointmentTypeRepository;
    @Mock private DoctorScheduleRepository doctorScheduleRepository;

    @Spy
    private AppointmentMapper mapper = Mappers.getMapper(AppointmentMapper.class);

    @InjectMocks
    private AppointmentServiceImpl service;


    private LocalDate futureDate() {
        return LocalDate.now().plusDays(1);
    }

    private LocalTime startTime() {
        return LocalTime.of(10, 0);
    }

    private DoctorSchedule schedule() {
        return DoctorSchedule.builder()
                .dayOfWeek(edu.unimag.domine.entities.enums.DayOfWeek.MONDAY)
                .startsAt(LocalTime.of(8, 0))
                .endsAt(LocalTime.of(18, 0))
                .build();
    }


    @Test
    void shouldCreateAppointmentSuccessfully() {

        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        UUID officeId = UUID.randomUUID();
        UUID appointmentTypeId = UUID.randomUUID();

        var request = new CreateAppointmentRequest(
                patientId, doctorId, officeId, appointmentTypeId,
                futureDate(), startTime(), "test"
        );

        var patient = Patient.builder().id(patientId).active(true).build();
        var doctor = Doctor.builder().id(doctorId).active(true).build();
        var office = Office.builder().id(officeId).active(true).build();
        var type = AppointmentType.builder().id(appointmentTypeId).durationMinutes(30).build();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(officeRepository.findById(officeId)).thenReturn(Optional.of(office));
        when(appointmentTypeRepository.findById(appointmentTypeId)).thenReturn(Optional.of(type));
        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(any(), any()))
                .thenReturn(Optional.of(schedule()));

        when(appointmentRepository.existsOverlapByDoctor(any(), any(), any(), any())).thenReturn(false);
        when(appointmentRepository.existsOverlapByOffice(any(), any(), any(), any())).thenReturn(false);
        when(appointmentRepository.existsOverlapByPatient(any(), any(), any(), any())).thenReturn(false);

        when(appointmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.create(request);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(Status.SCHEDULED);
    }


    @Test
    void shouldThrowWhenPastDate() {

        var request = new CreateAppointmentRequest(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.now().minusDays(1), startTime(), "test"
        );

        assertThrows(BusinessException.class, () -> service.create(request));
    }


    @Test
    void shouldThrowWhenPatientNotFound() {

        UUID patientId = UUID.randomUUID();

        var request = new CreateAppointmentRequest(
                patientId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                futureDate(), startTime(), "test"
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
        verify(appointmentRepository, never()).save(any());
    }


    @Test
    void shouldThrowWhenDoctorOverlap() {

        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        UUID officeId = UUID.randomUUID();
        UUID typeId = UUID.randomUUID();

        var request = new CreateAppointmentRequest(
                patientId, doctorId, officeId, typeId,
                futureDate(), startTime(), "test"
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(Patient.builder().active(true).build()));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(Doctor.builder().active(true).build()));
        when(officeRepository.findById(officeId)).thenReturn(Optional.of(Office.builder().active(true).build()));
        when(appointmentTypeRepository.findById(typeId)).thenReturn(Optional.of(AppointmentType.builder().durationMinutes(30).build()));
        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(any(), any()))
                .thenReturn(Optional.of(schedule()));

        when(appointmentRepository.existsOverlapByDoctor(any(), any(), any(), any()))
                .thenReturn(true);

        assertThrows(ConflictException.class, () -> service.create(request));
    }


    @Test
    void shouldGetById() {

        UUID id = UUID.randomUUID();

        var appointment = Appointment.builder()
                .id(id)
                .status(Status.SCHEDULED)
                .date(futureDate())
                .startAt(startTime())
                .endAt(startTime().plusMinutes(30))
                .build();

        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));

        var result = service.getById(id);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);
    }


    @Test
    void shouldConfirm() {

        UUID id = UUID.randomUUID();

        var appointment = Appointment.builder()
                .id(id)
                .status(Status.SCHEDULED)
                .date(futureDate())
                .startAt(startTime())
                .build();

        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.confirm(id);

        assertThat(result.status()).isEqualTo(Status.CONFIRMED);
    }


    @Test
    void shouldCancel() {

        UUID id = UUID.randomUUID();

        var appointment = Appointment.builder()
                .id(id)
                .status(Status.SCHEDULED)
                .date(futureDate())
                .startAt(startTime())
                .build();

        var request = new CancelAppointmentRequest("reason");

        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.cancel(id, request);

        assertThat(result.status()).isEqualTo(Status.CANCELLED);
    }


    @Test
    void shouldComplete() {

        UUID id = UUID.randomUUID();

        var appointment = Appointment.builder()
                .id(id)
                .status(Status.CONFIRMED)
                .date(LocalDate.now().minusDays(1))
                .startAt(startTime())
                .build();

        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.complete(id);

        assertThat(result.status()).isEqualTo(Status.COMPLETED);
    }


    @Test
    void shouldMarkNoShow() {

        UUID id = UUID.randomUUID();

        var appointment = Appointment.builder()
                .id(id)
                .status(Status.CONFIRMED)
                .date(LocalDate.now().minusDays(1))
                .startAt(startTime())
                .build();

        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.markAsNoShow(id);

        assertThat(result.status()).isEqualTo(Status.NO_SHOW);
    }
}