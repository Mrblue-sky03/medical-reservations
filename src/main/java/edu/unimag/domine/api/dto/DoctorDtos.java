package edu.unimag.domine.api.dto;

import edu.unimag.domine.entities.enums.DocumentType;

import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.util.UUID;

public class DoctorDtos {
    public record CreateDoctorRequest(
            @NotNull(message = "La especialidad es obligatoria")
            UUID specialtyId,

            @NotBlank(message = "El nombre completo es obligatorio")
            String fullName,

            @NotBlank(message = "El email es obligatorio")
            @Email(message = "El email no tiene un formato válido")
            String email,

            @NotBlank(message = "El teléfono es obligatorio")
            String phoneNumber,

            @NotBlank(message = "El número de documento es obligatorio")
            @Size(max = 20)
            String documentNumber,

            @NotNull(message = "El tipo de documento es obligatorio")
            DocumentType documentType,

            @NotBlank(message = "El número de licencia es obligatorio")

            String numberLicense
    ) implements Serializable {}

    public record UpdateDoctorRequest(
            UUID specialtyId,
            String fullName,
            @Email String email,
            String phoneNumber,
            String numberLicense,
            String documentNumber,
            Boolean active
    ) implements Serializable {}

    public record DoctorResponse(
            UUID id,
            UUID specialtyId,
            String fullName,
            String email,
            String phoneNumber,
            String documentNumber,
            DocumentType documentType,
            String numberLicense,
            Boolean active
    ) implements Serializable{}
}
