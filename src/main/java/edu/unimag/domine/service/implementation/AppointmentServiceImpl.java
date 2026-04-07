package edu.unimag.domine.service.implementation;


import edu.unimag.domine.api.dto.AppointmentDtos.AppointmentResponse;
import edu.unimag.domine.api.dto.AppointmentDtos.CancelAppointmentRequest;
import edu.unimag.domine.api.dto.AppointmentDtos.CreateAppointmentRequest;
import edu.unimag.domine.entities.*;
import edu.unimag.domine.entities.enums.Status;
import edu.unimag.domine.exceptions.BusinessException;
import edu.unimag.domine.exceptions.ConflictException;
import edu.unimag.domine.exceptions.ResourceNotFoundException;
import edu.unimag.domine.mappers.AppointmentMapper;
import edu.unimag.domine.repositories.*;
import edu.unimag.domine.service.AppointmentService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import edu.unimag.domine.entities.enums.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final OfficeRepository officeRepository;
    private final AppointmentTypeRepository appointmentTypeRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final AppointmentMapper mapper;

    @Override
    public AppointmentResponse create(CreateAppointmentRequest request) {

        requireNonNull(request, "Request cannot be null");

        if (request.date().isBefore(LocalDate.now()) ||
                (request.date().isEqual(LocalDate.now()) &&
                        request.startsAt().isBefore(LocalTime.now()))) {
            throw new BusinessException("Cannot create appointment in the past");
        }

        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        if (!Boolean.TRUE.equals(patient.getActive())) {
            throw new BusinessException("Patient is not active");
        }

        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        if (!Boolean.TRUE.equals(doctor.getActive())) {
            throw new BusinessException("Doctor is not active");
        }

        Office office = officeRepository.findById(request.officeId())
                .orElseThrow(() -> new ResourceNotFoundException("Office not found"));

        if (!Boolean.TRUE.equals(office.getActive())) {
            throw new BusinessException("Office is not active");
        }

        AppointmentType type = appointmentTypeRepository.findById(request.appointmentTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Type not found"));


        LocalTime endAt = request.startsAt().plusMinutes(type.getDurationMinutes());

        validateDoctorSchedule(doctor.getId(), request.date(), request.startsAt(), endAt);

        if (appointmentRepository.existsOverlapByDoctor(
                doctor.getId(), request.date(), request.startsAt(), endAt)) {
            throw new ConflictException("Doctor overlap");
        }

        if (appointmentRepository.existsOverlapByOffice(
                office.getId(), request.date(), request.startsAt(), endAt)) {
            throw new ConflictException("Office overlap");
        }

        if (appointmentRepository.existsOverlapByPatient(
                patient.getId(), request.date(), request.startsAt(), endAt)) {
            throw new ConflictException("Patient overlap");
        }

        Appointment appointment = mapper.toEntity(request);

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setOffice(office);
        appointment.setAppointmentType(type);
        appointment.setEndAt(endAt);
        appointment.setStatus(Status.SCHEDULED);
        appointment.setCreatedAt(Instant.now());

        return mapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse getById(UUID id) {

        requireNonNull(id, "Id cannot be null");

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        return mapper.toResponse(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAll() {

        return appointmentRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public AppointmentResponse confirm(UUID id) {

        Appointment appointment = findOrThrow(id);

        if (appointment.getStatus() != Status.SCHEDULED) {
            throw new BusinessException("Only SCHEDULED can be confirmed");
        }

        appointment.setStatus(Status.CONFIRMED);
        appointment.setUpdatedAt(Instant.now());

        return mapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentResponse cancel(UUID id, CancelAppointmentRequest request) {

        requireNonNull(request, "Request cannot be null");

        if (request.cancelReason() == null || request.cancelReason().isBlank()) {
            throw new IllegalArgumentException("Cancel reason required");
        }

        Appointment appointment = findOrThrow(id);

        if (appointment.getStatus() != Status.SCHEDULED &&
                appointment.getStatus() != Status.CONFIRMED) {
            throw new BusinessException("Cannot cancel this appointment");
        }

        appointment.setStatus(Status.CANCELLED);
        appointment.setCancelReason(request.cancelReason());
        appointment.setUpdatedAt(Instant.now());

        return mapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentResponse complete(UUID id) {

        Appointment appointment = findOrThrow(id);

        if (appointment.getStatus() != Status.CONFIRMED) {
            throw new BusinessException("Only CONFIRMED can be completed");
        }

        if (LocalDate.now().isBefore(appointment.getDate()) ||
                (LocalDate.now().isEqual(appointment.getDate()) &&
                        LocalTime.now().isBefore(appointment.getStartAt()))) {
            throw new BusinessException("Cannot complete before start");
        }

        appointment.setStatus(Status.COMPLETED);
        appointment.setUpdatedAt(Instant.now());

        return mapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentResponse markAsNoShow(UUID id) {

        Appointment appointment = findOrThrow(id);

        if (appointment.getStatus() != Status.CONFIRMED) {
            throw new BusinessException("Only CONFIRMED can be NO_SHOW");
        }

        if (LocalDate.now().isBefore(appointment.getDate()) ||
                (LocalDate.now().isEqual(appointment.getDate()) &&
                        LocalTime.now().isBefore(appointment.getStartAt()))) {
            throw new BusinessException("Cannot mark NO_SHOW before start");
        }

        appointment.setStatus(Status.NO_SHOW);
        appointment.setUpdatedAt(Instant.now());

        return mapper.toResponse(appointmentRepository.save(appointment));
    }



    private Appointment findOrThrow(UUID id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
    }

    private void validateDoctorSchedule(UUID doctorId, LocalDate date,
                                        LocalTime start, LocalTime end) {

        java.time.DayOfWeek javaDay = date.getDayOfWeek();

        DayOfWeek dayOfWeek = DayOfWeek.valueOf(javaDay.name());

        DoctorSchedule schedule = doctorScheduleRepository
                .findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek)
                .orElseThrow(() -> new BusinessException("No schedule"));

        if (start.isBefore(schedule.getStartsAt()) ||
                end.isAfter(schedule.getEndsAt())) {
            throw new BusinessException("Outside schedule");
        }
    }

    private void requireNonNull(Object obj, String msg) {
        if (obj == null) throw new IllegalArgumentException(msg);
    }
}
