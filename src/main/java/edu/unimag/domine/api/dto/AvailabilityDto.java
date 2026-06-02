package edu.unimag.domine.api.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;

public class AvailabilityDto {

    public record AvailabilitySlotResponse(
            LocalDate date,
            LocalTime startsAt,
            LocalTime endsAt
    ) implements Serializable {}

}
