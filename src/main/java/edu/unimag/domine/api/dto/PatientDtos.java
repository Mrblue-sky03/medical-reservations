package edu.unimag.domine.api.dto;

import edu.unimag.domine.entities.enums.DocumentType;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;


import jakarta.validation.constraints.*;

public class PatientDtos {

    public record CreatePatientRequest(
            @NotBlank(message = "El nombre completo es obligatorio")
            String fullName,

            @NotNull(message = "El tipo de documento es obligatorio")
            DocumentType documentType,

            @NotBlank(message = "El número de documento es obligatorio")
            String documentNumber,

            @NotBlank(message = "El email es obligatorio")
            @Email(message = "El email no tiene un formato válido")
            String email,

            @NotBlank(message = "El teléfono es obligatorio")
            String phoneNumber,

            @NotNull(message = "La fecha de nacimiento es obligatoria")
            @Past(message = "La fecha de nacimiento debe ser pasada")
            LocalDate birthDay
    ) implements Serializable {}

    public record UpdatePatientRequest(
            @NotBlank String fullName,
            @NotBlank @Email String email,
            @NotBlank String phoneNumber,
            @NotNull DocumentType documentType
    ) implements Serializable {}

    public record PatientResponse(
            UUID id,
            String fullName,
            DocumentType documentType,
            String documentNumber,
            String email,
            String phoneNumber,
            Boolean active,
            LocalDate birthDate
    ) implements Serializable {}
}


