package edu.unimag.domine.api.dto;

import edu.unimag.domine.entities.enums.Status;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class AppointmentDtos {

    public record CreateAppointmentRequest(
            @NotNull(message = "El paciente es obligatorio")
            UUID patientId,

            @NotNull(message = "El doctor es obligatorio")
            UUID doctorId,

            @NotNull(message = "El consultorio es obligatorio")
            UUID officeId,

            @NotNull(message = "El tipo de cita es obligatorio")
            UUID appointmentTypeId,

            @NotNull(message = "La fecha es obligatoria")
            @FutureOrPresent(message = "La fecha no puede ser pasada")
            LocalDate date,

            @NotNull(message = "La hora de inicio es obligatoria")
            LocalTime startsAt,

            @Size(max = 500, message = "Las observaciones no pueden superar 500 caracteres")
            String observations
    ) implements Serializable {}

    public record CancelAppointmentRequest(
            @NotBlank(message = "El motivo de cancelación es obligatorio")
            String cancelReason
    ) implements Serializable {}

    public record AppointmentResponse(
            UUID id,
            UUID patientId,
            UUID doctorId,
            UUID officeId,
            UUID appointmentTypeId,
            LocalDate date,
            LocalTime startAt,
            LocalTime endAt,
            Status status,
            String observations
    ) implements Serializable{}
}
