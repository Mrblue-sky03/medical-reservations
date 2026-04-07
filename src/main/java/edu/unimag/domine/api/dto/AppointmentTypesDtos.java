package edu.unimag.domine.api.dto;

import java.io.Serializable;
import java.util.UUID;

public class AppointmentTypesDtos {

    public record CreateAppointmentTypeRequest(
            String name,
            String description,
            Integer durationMinutes
    ) implements Serializable {}

    public record AppointmentTypeResponse(
            UUID id,
            String name,
            String description,
            Integer durationMinutes
    ) implements Serializable {}
}
