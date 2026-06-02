package edu.unimag.domine.api.dto;


import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.util.UUID;

public class OfficeDtos {


    public record CreateOfficeRequest(
            @NotBlank(message = "El nombre del consultorio es obligatorio")
            String name,

            @NotBlank(message = "La ubicación es obligatoria")
            String location
    ) implements Serializable {}

    public record UpdateOfficeRequest(
            String name,
            String location,
            Boolean active
    ) implements Serializable {}

    public record OfficeResponse(
            UUID id,
            String name,
            String location,
            Boolean active
    ) implements Serializable {}
}
