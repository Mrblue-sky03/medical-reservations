package edu.unimag.domine.api.dto;

import java.io.Serializable;
import java.util.UUID;

public class ReportsDtos {
    public record OfficeOccupancyResponse(
            UUID officeId,
            String name,
            Long totalAppointments
    ) implements Serializable {}

    public record DoctorProductivityResponse(
            UUID doctorId,
            String fullName,
            Long completedAppointments
    ) implements Serializable {}

    public record NoShowPatientResponse(
            UUID patientId,
            String fullName,
            Long noShowCount
    ) implements Serializable {}
}
