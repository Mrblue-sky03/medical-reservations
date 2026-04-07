package edu.unimag.domine.repositories;

import edu.unimag.domine.entities.*;
import edu.unimag.domine.entities.enums.DocumentType;
import edu.unimag.domine.entities.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AppointmentRepositoryTest extends AbstractIntegrationDBTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private OfficeRepository officeRepository;

    @Autowired
    private AppointmentTypeRepository appointmentTypeRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @BeforeEach
    void clean() {
        appointmentRepository.deleteAll();
        patientRepository.deleteAll();
        doctorRepository.deleteAll();
        officeRepository.deleteAll();
        appointmentTypeRepository.deleteAll();
        specialtyRepository.deleteAll();
    }

    // ─── Métodos helper ───────────────────────────────────────────

    private Specialty createSpecialty() {
        Specialty specialty = Specialty.builder()
                .name("Specialty " + UUID.randomUUID())
                .description("Description")
                .build();
        return specialtyRepository.save(specialty);
    }

    private Doctor createDoctor(Specialty specialty) {
        Doctor doctor = Doctor.builder()
                .fullName("Doctor " + UUID.randomUUID())
                .email("doctor" + UUID.randomUUID() + "@test.com")
                .documentType(DocumentType.CC)
                .documentNumber(UUID.randomUUID().toString().substring(0, 10))
                .licenseNumber(UUID.randomUUID().toString().substring(0, 10))
                .phoneNumber(UUID.randomUUID().toString().substring(0, 10))
                .active(true)
                .specialty(specialty)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return doctorRepository.save(doctor);
    }

    private Patient createPatient() {
        Patient patient = Patient.builder()
                .fullName("Patient " + UUID.randomUUID())
                .email("patient" + UUID.randomUUID() + "@test.com")
                .documentType(DocumentType.CC)
                .documentNumber(UUID.randomUUID().toString().substring(0, 10))
                .phoneNumber(UUID.randomUUID().toString().substring(0, 10))
                .active(true)
                .birthDate(LocalDate.of(1990, 1, 1))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return patientRepository.save(patient);
    }

    private Office createOffice() {
        Office office = Office.builder()
                .name("Office " + UUID.randomUUID())
                .location("Location")
                .description("description")
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return officeRepository.save(office);
    }

    private AppointmentType createAppointmentType() {
        AppointmentType appointmentType = AppointmentType.builder()
                .name("Type " + UUID.randomUUID())
                .description("Description")
                .durationMinutes(30)
                .build();
        return appointmentTypeRepository.save(appointmentType);
    }

    private Appointment createAppointment(
            Patient patient,
            Doctor doctor,
            Office office,
            AppointmentType appointmentType,
            LocalDate date,
            LocalTime startAt,
            LocalTime endAt,
            Status status
    ) {
        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .office(office)
                .appointmentType(appointmentType)
                .date(date)
                .startAt(startAt)
                .endAt(endAt)
                .status(status)
                .observations("")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return appointmentRepository.save(appointment);
    }

    // ─── Tests Query Methods ───────────────────────────────────────

    @Test
    void shouldFindByPatientIdAndStatus() {
        // arrange
        Patient patient = createPatient();
        Doctor doctor = createDoctor(createSpecialty());
        Office office = createOffice();
        AppointmentType type = createAppointmentType();

        Appointment scheduled = createAppointment(
                patient, doctor, office, type,
                LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(9, 30),
                Status.SCHEDULED
        );
        createAppointment(
                patient, doctor, office, type,
                LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(10, 30),
                Status.CANCELLED
        );

        // act
        List<Appointment> result = appointmentRepository
                .findByPatientIdAndStatus(patient.getId(), Status.SCHEDULED);

        // assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(scheduled.getId());
    }

    @Test
    void shouldFindByDateBetween() {
        // arrange
        Patient patient = createPatient();
        Doctor doctor = createDoctor(createSpecialty());
        Office office = createOffice();
        AppointmentType type = createAppointmentType();

        Appointment inRange = createAppointment(
                patient, doctor, office, type,
                LocalDate.of(2025, 1, 15), LocalTime.of(9, 0), LocalTime.of(9, 30),
                Status.SCHEDULED
        );
        createAppointment(
                patient, doctor, office, type,
                LocalDate.of(2025, 2, 1), LocalTime.of(9, 0), LocalTime.of(9, 30),
                Status.SCHEDULED
        );

        // act
        List<Appointment> result = appointmentRepository
                .findByDateBetween(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

        // assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(inRange.getId());
    }

    // ─── Tests JPQL ───────────────────────────────────────────────

    @Test
    void shouldDetectOverlapByDoctor() {
        // arrange
        Patient patient = createPatient();
        Doctor doctor = createDoctor(createSpecialty());
        Office office = createOffice();
        AppointmentType type = createAppointmentType();

        createAppointment(
                patient, doctor, office, type,
                LocalDate.of(2025, 1, 15), LocalTime.of(9, 0), LocalTime.of(9, 30),
                Status.SCHEDULED
        );

        // act — intenta crear una cita que se traslapa
        boolean overlaps = appointmentRepository.existsOverlapByDoctor(
                doctor.getId(),
                LocalDate.of(2025, 1, 15),
                LocalTime.of(9, 15),
                LocalTime.of(9, 45)
        );

        // assert
        assertThat(overlaps).isTrue();
    }

    @Test
    void shouldNotDetectOverlapByDoctorWhenNoOverlap() {
        // arrange
        Patient patient = createPatient();
        Doctor doctor = createDoctor(createSpecialty());
        Office office = createOffice();
        AppointmentType type = createAppointmentType();

        createAppointment(
                patient, doctor, office, type,
                LocalDate.of(2025, 1, 15), LocalTime.of(9, 0), LocalTime.of(9, 30),
                Status.SCHEDULED
        );

        // act — intenta crear una cita que NO se traslapa
        boolean overlaps = appointmentRepository.existsOverlapByDoctor(
                doctor.getId(),
                LocalDate.of(2025, 1, 15),
                LocalTime.of(9, 30),
                LocalTime.of(10, 0)
        );

        // assert
        assertThat(overlaps).isFalse();
    }

    @Test
    void shouldDetectOverlapByOffice() {
        // arrange
        Patient patient = createPatient();
        Doctor doctor = createDoctor(createSpecialty());
        Office office = createOffice();
        AppointmentType type = createAppointmentType();

        createAppointment(
                patient, doctor, office, type,
                LocalDate.of(2025, 1, 15), LocalTime.of(9, 0), LocalTime.of(9, 30),
                Status.SCHEDULED
        );

        // act
        boolean overlaps = appointmentRepository.existsOverlapByOffice(
                office.getId(),
                LocalDate.of(2025, 1, 15),
                LocalTime.of(9, 15),
                LocalTime.of(9, 45)
        );

        // assert
        assertThat(overlaps).isTrue();
    }

    @Test
    void shouldDetectOverlapByPatient() {
        // arrange
        Patient patient = createPatient();
        Doctor doctor = createDoctor(createSpecialty());
        Office office = createOffice();
        AppointmentType type = createAppointmentType();

        createAppointment(
                patient, doctor, office, type,
                LocalDate.of(2025, 1, 15), LocalTime.of(9, 0), LocalTime.of(9, 30),
                Status.SCHEDULED
        );

        // act
        boolean overlaps = appointmentRepository.existsOverlapByPatient(
                patient.getId(),
                LocalDate.of(2025, 1, 15),
                LocalTime.of(9, 15),
                LocalTime.of(9, 45)
        );

        // assert
        assertThat(overlaps).isTrue();
    }

    @Test
    void shouldRankDoctorsByCompletedAppointments() {
        // arrange
        Specialty specialty = createSpecialty();
        Doctor doctor1 = createDoctor(specialty);
        Doctor doctor2 = createDoctor(specialty);
        Patient patient = createPatient();
        Office office = createOffice();
        AppointmentType type = createAppointmentType();

        createAppointment(patient, doctor1, office, type,
                LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(9, 30), Status.COMPLETED);
        createAppointment(patient, doctor1, office, type,
                LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(10, 30), Status.COMPLETED);
        createAppointment(patient, doctor2, office, type,
                LocalDate.now(), LocalTime.of(11, 0), LocalTime.of(11, 30), Status.COMPLETED);

        // act
        List<Object[]> ranking = appointmentRepository.rankingDoctors();

        // assert
        assertThat(ranking).hasSize(2);
        assertThat(ranking.get(0)[0]).isEqualTo(doctor1.getId());
        assertThat((Long) ranking.get(0)[1]).isEqualTo(2L);
    }

    @Test
    void shouldCountCancelledAndNoShowBySpecialty() {
        // arrange
        Specialty specialty = createSpecialty();
        Doctor doctor = createDoctor(specialty);
        Patient patient = createPatient();
        Office office = createOffice();
        AppointmentType type = createAppointmentType();

        createAppointment(patient, doctor, office, type,
                LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(9, 30), Status.CANCELLED);
        createAppointment(patient, doctor, office, type,
                LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(10, 30), Status.NO_SHOW);
        createAppointment(patient, doctor, office, type,
                LocalDate.now(), LocalTime.of(11, 0), LocalTime.of(11, 30), Status.COMPLETED);

        // act
        List<Object[]> result = appointmentRepository.countCancelledAndNoShowBySpecialty();

        // assert
        assertThat(result).hasSize(1);
        assertThat((Long) result.get(0)[1]).isEqualTo(2L);
    }

    @Test
    void shouldFindTopNoShowPatients() {
        // arrange
        Patient patient1 = createPatient();
        Patient patient2 = createPatient();
        Doctor doctor = createDoctor(createSpecialty());
        Office office = createOffice();
        AppointmentType type = createAppointmentType();

        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 31);

        createAppointment(patient1, doctor, office, type,
                LocalDate.of(2025, 1, 10), LocalTime.of(9, 0), LocalTime.of(9, 30), Status.NO_SHOW);
        createAppointment(patient1, doctor, office, type,
                LocalDate.of(2025, 1, 11), LocalTime.of(9, 0), LocalTime.of(9, 30), Status.NO_SHOW);
        createAppointment(patient2, doctor, office, type,
                LocalDate.of(2025, 1, 12), LocalTime.of(9, 0), LocalTime.of(9, 30), Status.NO_SHOW);

        // act
        List<Object[]> result = appointmentRepository.topNoShowPatients(start, end);

        // assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0)[0]).isEqualTo(patient1.getId());
        assertThat((Long) result.get(0)[1]).isEqualTo(2L);
    }
}