package edu.unimag.domine.repositories;

import edu.unimag.domine.entities.Appointment;
import edu.unimag.domine.entities.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByPatientIdAndStatus(UUID patientId, Status status);

    List<Appointment> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Appointment> findByDoctorIdAndDate(UUID doctorId, LocalDate date);

    List<Appointment> findByDoctorIdAndDateAndStartAtBetween(
            UUID doctorId,
            LocalDate date,
            LocalTime start,
            LocalTime end
    );

    List<Appointment> findByOfficeIdAndDateAndStartAtBetween(
            UUID officeId,
            LocalDate date,
            LocalTime start,
            LocalTime end
    );

    @Query("""
    SELECT (COUNT(a) > 0)
    FROM Appointment a
    WHERE a.doctor.id = :doctorId
      AND a.date = :date
      AND a.startAt < :endAt
      AND a.endAt > :startAt""")
    boolean existsOverlapByDoctor(
            @Param("doctorId") UUID officeId,
            @Param("date") LocalDate date,
            @Param("startAt") LocalTime startAt,
            @Param("endAt") LocalTime endAt
    );

    @Query("""
    SELECT (COUNT(a) > 0)
    FROM Appointment a
    WHERE a.office.id = :officeId
      AND a.date = :date
      AND a.startAt < :endAt
      AND a.endAt > :startAt""")
    boolean existsOverlapByOffice(
            @Param("officeId") UUID officeId,
            @Param("date") LocalDate date,
            @Param("startAt") LocalTime startAt,
            @Param("endAt") LocalTime endAt
    );

    @Query("""
    SELECT (COUNT(a) > 0)
    FROM Appointment a
    WHERE a.patient.id = :patientId
      AND a.date = :date
      AND a.startAt < :endAt
      AND a.endAt > :startAt""")
    boolean existsOverlapByPatient(
            @Param("patientId") UUID patientId,
            @Param("date") LocalDate date,
            @Param("startAt") LocalTime startAt,
            @Param("endAt") LocalTime endAt
    );

    @Query("""
SELECT a.doctor.specialty.id, COUNT(a)
FROM Appointment a
WHERE a.status IN ('CANCELLED', 'NO_SHOW')
GROUP BY a.doctor.specialty.id
""")
    List<Object[]> countCancelledAndNoShowBySpecialty();

    @Query("""
SELECT a.doctor.id, COUNT(a)
FROM Appointment a
WHERE a.status = 'COMPLETED'
GROUP BY a.doctor.id
ORDER BY COUNT(a) DESC
""")
    List<Object[]> rankingDoctors();

    @Query("""
SELECT a.patient.id, COUNT(a)
FROM Appointment a
WHERE a.status = 'NO_SHOW'
AND a.date BETWEEN :start AND :end
GROUP BY a.patient.id
ORDER BY COUNT(a) DESC
""")
    List<Object[]> topNoShowPatients(LocalDate start, LocalDate end);


}
