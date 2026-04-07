package edu.unimag.domine.service;

import edu.unimag.domine.api.dto.PatientDtos.PatientResponse;
import edu.unimag.domine.api.dto.PatientDtos.CreatePatientRequest;
import edu.unimag.domine.api.dto.PatientDtos.UpdatePatientRequest;

import java.util.UUID;

public interface PatientService {

    PatientResponse create(CreatePatientRequest req);
    PatientResponse getById(UUID id);
    PatientResponse update(UUID id, UpdatePatientRequest request);

}
