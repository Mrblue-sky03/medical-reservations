package edu.unimag.domine.repositories;

import edu.unimag.domine.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {

    List<Patient> findByActive(Boolean active);

    boolean existsByDocumentNumber(String documentNumber);
}
