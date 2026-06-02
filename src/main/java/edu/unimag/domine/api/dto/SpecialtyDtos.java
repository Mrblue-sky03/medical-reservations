package edu.unimag.domine.api.dto;

import java.io.Serializable;
import java.util.UUID;
import jakarta.validation.constraints.*;


public class SpecialtyDtos {

    public record CreateSpecialtyRequest(
            @NotBlank(message = "El nombre de la especialidad es obligatorio")
            String name,
            String description
    ) implements Serializable {}

    public record UpdateSpecialtyRequest(
            String name,
            String description
    ) implements Serializable {}

    public record SpecialtyResponse(
            UUID id,
            String name,
            String description
    ) implements Serializable {}
}