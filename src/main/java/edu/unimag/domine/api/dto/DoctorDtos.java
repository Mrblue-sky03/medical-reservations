package edu.unimag.domine.api.dto;

import edu.unimag.domine.entities.enums.DocumentType;

import java.io.Serializable;
import java.util.UUID;

public class DoctorDtos {
    public record CreateDoctorRequest(
            UUID specialtyId,
            String fullName,
            String email,
            String phoneNumber,
            String documentNumber,
            DocumentType documentType,
            String numberLicense
    ) implements Serializable {}

    public record UpdateDoctorRequest(
            UUID specialtyId,
            String fullName,
            String email,
            String phoneNumber,
            String numberLicense,
            String documentNumber,
            Boolean active
    ) implements Serializable{}

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
