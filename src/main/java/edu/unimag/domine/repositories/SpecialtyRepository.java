package edu.unimag.domine.repositories;

import edu.unimag.domine.entities.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpecialtyRepository extends JpaRepository<Specialty, UUID> {
    boolean existsByName(String name);
}
