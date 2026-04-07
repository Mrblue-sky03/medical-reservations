package edu.unimag.domine.repositories;

import edu.unimag.domine.entities.Patient;
import edu.unimag.domine.entities.enums.DocumentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PatientRepositoryTest extends AbstractIntegrationDBTest {

    @Autowired
    private PatientRepository patientRepository;

    @BeforeEach
    void clean() {
        patientRepository.deleteAll();
    }

    private Patient createPatient(boolean active) {
        Patient patient = Patient.builder()
                .fullName("Patient " + UUID.randomUUID())
                .email("patient" + UUID.randomUUID() + "@test.com")
                .documentNumber(UUID.randomUUID().toString().substring(0, 10))
                .phoneNumber(UUID.randomUUID().toString().substring(0, 10))
                .documentType(DocumentType.CC)
                .active(active)
                .birthDate(LocalDate.of(1990, 1, 1))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return patientRepository.save(patient);
    }

    @Test
    void shouldFindByActive() {

        Patient activePatient = createPatient(true);
        createPatient(false);

        List<Patient> result = patientRepository.findByActive(true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(activePatient.getId());
    }

    @Test
    void shouldReturnTrueWhenDocumentNumberExists() {

        Patient patient = createPatient(true);

        boolean result = patientRepository
                .existsByDocumentNumber(patient.getDocumentNumber());

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenDocumentNumberNotExists() {

        createPatient(true);

        boolean result = patientRepository.existsByDocumentNumber("nonexistent");

        assertThat(result).isFalse();
    }
}