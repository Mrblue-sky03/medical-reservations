package edu.unimag.domine.repositories;

import edu.unimag.domine.entities.AppointmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AppointmentTypeRepository extends JpaRepository<AppointmentType, UUID> {
    boolean existsByName(String name);
}
