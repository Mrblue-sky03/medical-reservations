package edu.unimag.domine.api.dto;

import edu.unimag.domine.entities.enums.DayOfWeek;

import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.UUID;

public class DoctorScheduleDtos {
    public record CreateDoctorScheduleRequest(
            @NotNull(message = "El doctor es obligatorio")
            UUID doctorId,

            @NotNull(message = "El día de la semana es obligatorio")
            DayOfWeek dayOfWeek,

            @NotNull(message = "La hora de inicio es obligatoria")
            LocalTime startAt,

            @NotNull(message = "La hora de fin es obligatoria")
            LocalTime endAt
    ) implements Serializable {}

    public record DoctorScheduleResponse(
            UUID id,
            UUID doctorId,
            DayOfWeek dayOfWeek,
            LocalTime startsAt,
            LocalTime endsAt
    ) implements Serializable {}
}
