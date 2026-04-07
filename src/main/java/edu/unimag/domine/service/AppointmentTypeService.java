package edu.unimag.domine.service;

import edu.unimag.domine.api.dto.AppointmentTypesDtos.CreateAppointmentTypeRequest;
import edu.unimag.domine.api.dto.AppointmentTypesDtos.AppointmentTypeResponse;

import java.util.List;

public interface AppointmentTypeService {
        AppointmentTypeResponse create(CreateAppointmentTypeRequest req);
        List<AppointmentTypeResponse> getAll();
}
