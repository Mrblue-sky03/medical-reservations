package edu.unimag.domine.service.implementation;

import edu.unimag.domine.api.dto.ReportsDtos.DoctorProductivityResponse;
import edu.unimag.domine.api.dto.ReportsDtos.NoShowPatientResponse;
import edu.unimag.domine.api.dto.ReportsDtos.OfficeOccupancyResponse;
import edu.unimag.domine.entities.Appointment;
import edu.unimag.domine.entities.Doctor;
import edu.unimag.domine.entities.Patient;
import edu.unimag.domine.entities.enums.Status;
import edu.unimag.domine.exceptions.ValidationException;
import edu.unimag.domine.repositories.AppointmentRepository;
import edu.unimag.domine.repositories.DoctorRepository;
import edu.unimag.domine.repositories.PatientRepository;
import edu.unimag.domine.service.ReportsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportsServiceImpl implements ReportsService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Override
    public List<OfficeOccupancyResponse> getOfficeOccupancy(LocalDate date, LocalTime startAt, LocalTime endAt) {
        if (date == null || startAt == null || endAt == null) {
            throw new ValidationException("date and hour can not be null");
        }

        // Filtramos las citas en el rango de tiempo y que no estén canceladas/ausentes
        List<Appointment> appointments = appointmentRepository.findByDateBetween(date, date)
                .stream()
                .filter(a -> a.getStatus() != Status.CANCELLED && a.getStatus() != Status.NO_SHOW)
                .filter(a -> !a.getStartAt().isBefore(startAt) && !a.getEndAt().isAfter(endAt))
                .toList();

        // Agrupamos usando el objeto Office completo para poder acceder a su nombre fácilmente
        Map<edu.unimag.domine.entities.Office, Long> occupancyMap = appointments.stream()
                .collect(Collectors.groupingBy(Appointment::getOffice, Collectors.counting()));

        return occupancyMap.entrySet().stream()
                .map(entry -> new OfficeOccupancyResponse(
                        entry.getKey().getId(),
                        entry.getKey().getName(),
                        entry.getValue()
                ))
                .toList();
    }

    @Override
    public List<DoctorProductivityResponse> getDoctorProductivity() {
        List<Object[]> ranking = appointmentRepository.rankingDoctors();

        return ranking.stream()
                .map(row -> {
                    UUID doctorId = (UUID) row[0];
                    Long completedCount = (Long) row[1];

                    // Buscamos al doctor para obtener su fullName
                    Doctor doctor = doctorRepository.findById(doctorId).orElse(null);

                    String fullName = (doctor != null)
                            ? doctor.getFullName()
                            : "doctor not found";

                    return new DoctorProductivityResponse(doctorId, fullName, completedCount);
                })
                .toList();
    }

    @Override
    public List<NoShowPatientResponse> getNoShowPatients(LocalDate date, LocalTime startAt, LocalTime endAt) {
        if (date == null) {
            throw new ValidationException("date can not be null");
        }

        List<Object[]> noShows = appointmentRepository.topNoShowPatients(date, date);

        return noShows.stream()
                .map(row -> {
                    UUID patientId = (UUID) row[0];
                    Long noShowCount = (Long) row[1];

                    // Buscamos al paciente para obtener su fullName
                    Patient patient = patientRepository.findById(patientId).orElse(null);

                    String fullName = (patient != null)
                            ? patient.getFullName()
                            : "patient not found";

                    return new NoShowPatientResponse(patientId, fullName, noShowCount);
                })
                .toList();
    }
}