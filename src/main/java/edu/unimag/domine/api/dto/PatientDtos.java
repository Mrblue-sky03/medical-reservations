package edu.unimag.domine.api.dto;

import edu.unimag.domine.entities.enums.DocumentType;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public class PatientDtos {

    public record CreatePatientRequest(
            String fullName,
            DocumentType documentType,
            String documentNumber,
            String email,
            String phoneNumber,
            LocalDate birthDay
    ) implements Serializable {}

    public record UpdatePatientRequest(
            String fullName,
            String email,
            String phoneNumber,
            DocumentType documentType

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


