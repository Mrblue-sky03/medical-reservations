package edu.unimag.domine.service.implementation;

import edu.unimag.domine.api.dto.DoctorScheduleDtos.CreateDoctorScheduleRequest;
import edu.unimag.domine.api.dto.DoctorScheduleDtos.DoctorScheduleResponse;
import edu.unimag.domine.entities.Doctor;
import edu.unimag.domine.entities.DoctorSchedule;
import edu.unimag.domine.exceptions.ConflictException;
import edu.unimag.domine.exceptions.ResourceNotFoundException;
import edu.unimag.domine.exceptions.ValidationException;
import edu.unimag.domine.mappers.DoctorScheduleMapper;
import edu.unimag.domine.repositories.DoctorRepository; // Asegúrate de tener este repositorio
import edu.unimag.domine.repositories.DoctorScheduleRepository;
import edu.unimag.domine.service.DoctorScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    private final DoctorScheduleRepository doctorScheduleRepository;
    private final DoctorRepository doctorRepository; // Necesario para buscar al doctor
    private final DoctorScheduleMapper doctorScheduleMapper;

    @Override
    public DoctorScheduleResponse create(UUID doctorId, CreateDoctorScheduleRequest request) {

        if (request.startAt().isAfter(request.endAt())) {
            throw new ValidationException("La hora de inicio no puede ser posterior a la hora de fin");
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró un doctor con el ID: " + doctorId));

        doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, request.dayOfWeek())
                .ifPresent(schedule -> {
                    throw new ConflictException("El doctor ya tiene un horario configurado para el día: " + request.dayOfWeek());
                });

        DoctorSchedule schedule = doctorScheduleMapper.toEntity(request);

        schedule.setDoctor(doctor);
        schedule.setStartsAt(request.startAt());
        schedule.setEndsAt(request.endAt());

        DoctorSchedule savedSchedule = doctorScheduleRepository.save(schedule);
        return doctorScheduleMapper.toResponse(savedSchedule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorScheduleResponse> getAllSchedules(UUID doctorId) {
        return doctorScheduleRepository.findByDoctorId(doctorId)
                .stream()
                .map(doctorScheduleMapper::toResponse)
                .toList();
    }
}