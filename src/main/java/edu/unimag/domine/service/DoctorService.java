package edu.unimag.domine.service;

import edu.unimag.domine.api.dto.DoctorDtos.CreateDoctorRequest;
import edu.unimag.domine.api.dto.DoctorDtos.DoctorResponse;
import edu.unimag.domine.api.dto.DoctorDtos.UpdateDoctorRequest;


import java.util.List;
import java.util.UUID;

public interface DoctorService {

    DoctorResponse create(CreateDoctorRequest req);
    DoctorResponse getDoctorById(UUID id);
    List<DoctorResponse> getAllDoctors();
    DoctorResponse updateDoctor(UUID id, UpdateDoctorRequest req);
}
