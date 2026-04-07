package edu.unimag.domine.repositories;

import edu.unimag.domine.entities.Doctor;
import edu.unimag.domine.entities.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    List<Doctor> findBySpecialtyAndActiveTrue(Specialty specialty);

    List<Doctor> findByActive(Boolean active);

}
