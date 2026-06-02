package edu.unimag.domine.repositories;

import edu.unimag.domine.entities.Doctor;
import edu.unimag.domine.entities.Specialty;
import edu.unimag.domine.entities.enums.DocumentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DoctorRepositoryTest extends AbstractIntegrationDBTest {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @BeforeEach
    void clean() {
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

    private Doctor createDoctor(Specialty specialty, boolean active) {
        Doctor doctor = Doctor.builder()
                .fullName("Doctor " + UUID.randomUUID())
                .email("doctor" + UUID.randomUUID() + "@test.com")
                .documentNumber(UUID.randomUUID().toString().substring(0, 10))
                .licenseNumber(UUID.randomUUID().toString().substring(0, 10))
                .phoneNumber(UUID.randomUUID().toString().substring(0, 10))
                .documentType(DocumentType.CC)
                .active(active)
                .specialty(specialty)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return doctorRepository.save(doctor);
    }

    @Test
    void shouldFindBySpecialtyAndActiveTrue() {
        Specialty specialty1 = createSpecialty();
        Specialty specialty2 = createSpecialty();

        Doctor activeDoctor = createDoctor(specialty1, true);
        createDoctor(specialty1, false);
        createDoctor(specialty2, true);

        List<Doctor> result = doctorRepository.findBySpecialtyAndActiveTrue(specialty1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(activeDoctor.getId());
    }

    @Test
    void shouldFindByActive() {
        Specialty specialty = createSpecialty();

        Doctor activeDoctor = createDoctor(specialty, true);
        createDoctor(specialty, false);

        List<Doctor> result = doctorRepository.findByActive(true);

        // assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(activeDoctor.getId());
    }

    @Test
    void shouldReturnAllInactiveDoctors() {
        Specialty specialty = createSpecialty();

        createDoctor(specialty, true);
        Doctor inactiveDoctor = createDoctor(specialty, false);

        List<Doctor> result = doctorRepository.findByActive(false);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(inactiveDoctor.getId());
    }
}