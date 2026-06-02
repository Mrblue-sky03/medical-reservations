package edu.unimag.domine.api.dto;


import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.util.UUID;

public class AppointmentTypesDtos {

    public record CreateAppointmentTypeRequest(
            @NotBlank String name,
            String description,
            @NotNull
            @Positive
            Integer durationMinutes

    ) implements Serializable {}

    public record AppointmentTypeResponse(
            UUID id,
            String name,
            String description,
            Integer durationMinutes
    ) implements Serializable {}
}
