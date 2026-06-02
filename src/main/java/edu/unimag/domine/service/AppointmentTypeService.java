package edu.unimag.domine.service;

import edu.unimag.domine.api.dto.AppointmentTypesDtos.CreateAppointmentTypeRequest;
import edu.unimag.domine.api.dto.AppointmentTypesDtos.UpdateAppointmentTypeRequest;
import edu.unimag.domine.api.dto.AppointmentTypesDtos.AppointmentTypeResponse;

import java.util.List;
import java.util.UUID;

public interface AppointmentTypeService {
    AppointmentTypeResponse create(CreateAppointmentTypeRequest req);
    List<AppointmentTypeResponse> getAll();
    AppointmentTypeResponse getById(UUID id);
    AppointmentTypeResponse update(UUID id, UpdateAppointmentTypeRequest req); 
    void delete(UUID id);                                                    
}