package edu.unimag.domine.repositories;

import edu.unimag.domine.entities.Doctor;
import edu.unimag.domine.entities.DoctorSchedule;
import edu.unimag.domine.entities.Specialty;
import edu.unimag.domine.entities.enums.DayOfWeek;
import edu.unimag.domine.entities.enums.DocumentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DoctorScheduleRepositoryTest extends AbstractIntegrationDBTest {

    @Autowired
    private DoctorScheduleRepository doctorScheduleRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @BeforeEach
    void clean() {
        doctorScheduleRepository.deleteAll();
        doctorRepository.deleteAll();
        specialtyRepository.deleteAll();
    }

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
                .documentNumber(UUID.randomUUID().toString().substring(0, 10))
                .licenseNumber(UUID.randomUUID().toString().substring(0, 10))
                .phoneNumber(UUID.randomUUID().toString().substring(0, 10))
                .documentType(DocumentType.CC)
                .active(true)
                .specialty(specialty)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return doctorRepository.save(doctor);
    }

    private DoctorSchedule createSchedule(Doctor doctor, DayOfWeek day) {
        DoctorSchedule schedule = DoctorSchedule.builder()
                .doctor(doctor)
                .dayOfWeek(day)
                .startsAt(LocalTime.of(8, 0))
                .endsAt(LocalTime.of(17, 0))
                .build();
        return doctorScheduleRepository.save(schedule);
    }

    @Test
    void shouldFindByDoctorIdAndDayOfWeek() {
        // arrange
        Specialty specialty = createSpecialty();
        Doctor doctor = createDoctor(specialty);

        DoctorSchedule monday = createSchedule(doctor, DayOfWeek.MONDAY);
        createSchedule(doctor, DayOfWeek.TUESDAY);

        // act
        Optional<DoctorSchedule> result = doctorScheduleRepository
                .findByDoctorIdAndDayOfWeek(doctor.getId(), DayOfWeek.MONDAY);

        // assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(monday.getId());
    }

    @Test
    void shouldReturnEmptyWhenDoctorHasNoScheduleForDay() {
        // arrange
        Specialty specialty = createSpecialty();
        Doctor doctor = createDoctor(specialty);

        createSchedule(doctor, DayOfWeek.MONDAY);

        // act
        Optional<DoctorSchedule> result = doctorScheduleRepository
                .findByDoctorIdAndDayOfWeek(doctor.getId(), DayOfWeek.WEDNESDAY);

        // assert
        assertThat(result).isEmpty();
    }
}