package edu.unimag.domine.service;

import edu.unimag.domine.entities.Appointment;
import edu.unimag.domine.entities.Doctor;
import edu.unimag.domine.entities.Office;
import edu.unimag.domine.entities.Patient;
import edu.unimag.domine.entities.enums.Status;
import edu.unimag.domine.exceptions.ValidationException;
import edu.unimag.domine.repositories.AppointmentRepository;
import edu.unimag.domine.repositories.DoctorRepository;
import edu.unimag.domine.repositories.PatientRepository;
import edu.unimag.domine.service.implementation.ReportsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportsServiceImplTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private PatientRepository patientRepository;

    @InjectMocks private ReportsServiceImpl service;

    private final LocalDate date = LocalDate.now();
    private final LocalTime start = LocalTime.of(8, 0);
    private final LocalTime end = LocalTime.of(18, 0);
    private final UUID entityId = UUID.randomUUID();

    @Test
    void shouldGetOfficeOccupancy() {
        var office = Office.builder().id(entityId).name("Office A").build();
        var appointment = Appointment.builder()
                .office(office)
                .status(Status.SCHEDULED)
                .startAt(LocalTime.of(10, 0))
                .endAt(LocalTime.of(10, 30))
                .build();

        when(appointmentRepository.findByDateBetween(date, date)).thenReturn(List.of(appointment));

        var result = service.getOfficeOccupancy(date, start, end);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).officeId()).isEqualTo(entityId);
        assertThat(result.get(0).name()).isEqualTo("Office A");
        assertThat(result.get(0).totalAppointments()).isEqualTo(1L);
    }

    @Test
    void shouldThrowValidationExceptionWhenOccupancyParamsNull() {
        assertThrows(ValidationException.class, () -> service.getOfficeOccupancy(null, start, end));
    }

    @Test
    void shouldGetDoctorProductivity() {
        Object[] row = new Object[]{entityId, 5L};

        when(appointmentRepository.rankingDoctors()).thenReturn(Collections.singletonList(row));

        var doctor = Doctor.builder().id(entityId).fullName("John").build();
        when(doctorRepository.findById(entityId)).thenReturn(Optional.of(doctor));

        var result = service.getDoctorProductivity();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).doctorId()).isEqualTo(entityId);
        assertThat(result.get(0).completedAppointments()).isEqualTo(5L);
    }

    @Test
    void shouldGetNoShowPatients() {
        Object[] row = new Object[]{entityId, 3L};

        when(appointmentRepository.topNoShowPatients(date, date)).thenReturn(Collections.singletonList(row));

        var patient = Patient.builder().id(entityId).fullName("Jane").build();
        when(patientRepository.findById(entityId)).thenReturn(Optional.of(patient));

        var result = service.getNoShowPatients(date, start, end);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).patientId()).isEqualTo(entityId);
        assertThat(result.get(0).noShowCount()).isEqualTo(3L);
    }

    @Test
    void shouldThrowValidationExceptionWhenNoShowDateNull() {
        assertThrows(ValidationException.class, () -> service.getNoShowPatients(null, start, end));
    }
}