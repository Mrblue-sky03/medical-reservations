package edu.unimag.domine.service;

import edu.unimag.domine.api.dto.DoctorScheduleDtos.DoctorScheduleResponse;
import edu.unimag.domine.api.dto.DoctorScheduleDtos.CreateDoctorScheduleRequest;

import java.util.List;
import java.util.UUID;

public interface DoctorScheduleService {
    DoctorScheduleResponse create(UUID doctorId, CreateDoctorScheduleRequest request);
    List<DoctorScheduleResponse> getAllSchedules(UUID doctorId);

}
