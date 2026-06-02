package edu.unimag.domine.service.implementation;

import edu.unimag.domine.api.dto.DoctorDtos.CreateDoctorRequest;
import edu.unimag.domine.api.dto.DoctorDtos.DoctorResponse;
import edu.unimag.domine.api.dto.DoctorDtos.UpdateDoctorRequest;
import edu.unimag.domine.entities.Doctor;
import edu.unimag.domine.entities.Specialty;
import edu.unimag.domine.exceptions.ResourceNotFoundException;
import edu.unimag.domine.exceptions.ValidationException;
import edu.unimag.domine.mappers.DoctorMapper;
import edu.unimag.domine.repositories.DoctorRepository;
import edu.unimag.domine.repositories.SpecialtyRepository;
import edu.unimag.domine.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final SpecialtyRepository specialtyService;

    @Override
    public DoctorResponse create(CreateDoctorRequest req) {
        if (Objects.isNull(req)) {
            throw new ValidationException("request can not be null");
        }
        Specialty specialty = specialtyService.findById(req.specialtyId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found " + req.specialtyId()));

        Doctor doctor = doctorMapper.toEntity(req);
        doctor.setActive(true);                      
        doctor.setCreatedAt(Instant.now());
        doctor.setSpecialty(specialty);
        doctor.setLicenseNumber(req.numberLicense());
        return doctorMapper.toResponse(doctorRepository.save(doctor));
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getDoctorById(UUID id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found " + id));
        return doctorMapper.toResponse(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponse> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(doctorMapper::toResponse)
                .toList();
    }

   @Override
    public DoctorResponse updateDoctor(UUID id, UpdateDoctorRequest req) {
        
        if (Objects.isNull(req)) {
            throw new ValidationException("updated request can not be null");
        }

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("doctor not found " + id));

        doctorMapper.update(req, doctor);

        if (req.specialtyId() != null) {
            Specialty specialty = specialtyService.findById(req.specialtyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Specialty not found " + req.specialtyId()));
            doctor.setSpecialty(specialty);
        }

        if (req.numberLicense() != null) {
            doctor.setLicenseNumber(req.numberLicense());
        }

        if (req.active() != null) {
            doctor.setActive(req.active());
        }

        return doctorMapper.toResponse(doctorRepository.save(doctor));
    }
}