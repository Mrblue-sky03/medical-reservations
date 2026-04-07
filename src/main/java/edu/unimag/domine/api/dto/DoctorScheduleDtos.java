package edu.unimag.domine.api.dto;

import edu.unimag.domine.entities.Doctor;
import edu.unimag.domine.entities.enums.DayOfWeek;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.UUID;

public class DoctorScheduleDtos {
    public record CreateDoctorScheduleRequest(
            UUID doctorId,
            DayOfWeek dayOfWeek,
            LocalTime startAt,
            LocalTime endAt
    ) implements Serializable {}

    public record DoctorScheduleResponse(
            UUID doctorId,
            DayOfWeek dayOfWeek,
            LocalTime startsAt,
            LocalTime endsAt
    ) implements Serializable {}
}
