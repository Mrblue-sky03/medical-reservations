package edu.unimag.domine.service.implementation;

import edu.unimag.domine.api.dto.PatientDtos.CreatePatientRequest;
import edu.unimag.domine.api.dto.PatientDtos.PatientResponse;
import edu.unimag.domine.api.dto.PatientDtos.UpdatePatientRequest;
import edu.unimag.domine.entities.Patient;
import edu.unimag.domine.exceptions.ResourceNotFoundException;
import edu.unimag.domine.exceptions.ValidationException;
import edu.unimag.domine.mappers.PatientMapper;
import edu.unimag.domine.repositories.PatientRepository;
import edu.unimag.domine.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Override
    public PatientResponse create(CreatePatientRequest req) {
        if (Objects.isNull(req)) {
            throw new ValidationException("request can not be null");
        }
        Patient patient = patientMapper.toEntity(req);
        return patientMapper.toResponse(patientRepository.save(patient));
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getById(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("patient not found " + id));
        return patientMapper.toResponse(patient);
    }

    @Override
    public PatientResponse update(UUID id, UpdatePatientRequest request) {
        if (Objects.isNull(request)) {
            throw new ValidationException("updated request can not be null");
        }

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("patient not found " + id));

        patientMapper.update(request, patient);
        return patientMapper.toResponse(patientRepository.save(patient));
    }
}