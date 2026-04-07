package edu.unimag.domine.repositories;

import edu.unimag.domine.entities.Office;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OfficeRepository extends JpaRepository<Office, UUID> {

    boolean existsByActive(Boolean active);

    boolean existsByLocation(String location);
}
