package edu.unimag.domine.repositories;

import edu.unimag.domine.entities.DoctorSchedule;
import edu.unimag.domine.entities.enums.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, UUID> {
    Optional<DoctorSchedule> findByDoctorIdAndDayOfWeek(UUID id, DayOfWeek dayOfWeek);
    List<DoctorSchedule> findByDoctorId(UUID doctorId);
}
