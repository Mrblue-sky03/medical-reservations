package edu.unimag.domine.service.implementation;

import edu.unimag.domine.api.dto.AppointmentTypesDtos.CreateAppointmentTypeRequest;
import edu.unimag.domine.api.dto.AppointmentTypesDtos.AppointmentTypeResponse;
import edu.unimag.domine.entities.AppointmentType;
import edu.unimag.domine.exceptions.ConflictException;
import edu.unimag.domine.exceptions.ValidationException;
import edu.unimag.domine.mappers.AppointmentTypeMapper;
import edu.unimag.domine.repositories.AppointmentTypeRepository;
import edu.unimag.domine.service.AppointmentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static java.util.Objects.requireNonNull;

@Service
@Transactional
@RequiredArgsConstructor
public class AppointmentTypeServiceImpl implements AppointmentTypeService {
    private final AppointmentTypeRepository appointmentTypeRepository;
    private final AppointmentTypeMapper appointmentTypeMapper;

    @Override
    public AppointmentTypeResponse create(CreateAppointmentTypeRequest req) {

        if (req == null) {
            throw new ValidationException("The request cannot be null");
        }

        requireNonBlank(req.name(), "The appointment type name cannot be blank");

        if (req.durationMinutes() == null) {
            throw new ValidationException("The duration cannot be null");
        }

        if (req.durationMinutes() <= 0) {
            throw new ValidationException("The duration must be greater than 0");
        }

        if (appointmentTypeRepository.existsByName(req.name())) {
            throw new ConflictException("An appointment type with name '" + req.name() + "' already exists");
        }

        AppointmentType appointmentType = appointmentTypeMapper.toEntity(req);
        return appointmentTypeMapper.toResponse(appointmentTypeRepository.save(appointmentType));
    }


    private static void requireNonBlank(String str, String message) {
        if (str == null || str.trim().isEmpty()) {
            throw new ValidationException(message);
        }
    }
    @Transactional(readOnly = true)
    @Override
    public List<AppointmentTypeResponse> getAll() {
        return appointmentTypeRepository.findAll()
                .stream()
                .map(appointmentTypeMapper::toResponse)
                .toList();
    }
}
