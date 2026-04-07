package edu.unimag.domine.api.dto;

import edu.unimag.domine.entities.enums.Status;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class AppointmentDtos {

    public record CreateAppointmentRequest(
            UUID patientId,
            UUID doctorId,
            UUID officeId,
            UUID appointmentTypeId,
            LocalDate date,
            LocalTime startsAt,
            String observations
    ) implements Serializable {}

    public record CancelAppointmentRequest(
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
