package edu.unimag.domine.service;

import edu.unimag.domine.api.dto.AppointmentDtos.CancelAppointmentRequest;
import edu.unimag.domine.api.dto.AppointmentDtos.CreateAppointmentRequest;
import edu.unimag.domine.api.dto.AppointmentDtos.AppointmentResponse;

import java.util.List;
import java.util.UUID;

public interface AppointmentService {

        AppointmentResponse getById(UUID id);
        List<AppointmentResponse> getAll();

        AppointmentResponse create(CreateAppointmentRequest req);
        AppointmentResponse cancel(UUID id, CancelAppointmentRequest req);
        AppointmentResponse complete(UUID id);
        AppointmentResponse markAsNoShow(UUID id);
        AppointmentResponse confirm(UUID id);

}
