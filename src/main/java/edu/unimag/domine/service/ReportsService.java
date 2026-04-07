package edu.unimag.domine.service;

import edu.unimag.domine.api.dto.ReportsDtos.OfficeOccupancyResponse;
import edu.unimag.domine.api.dto.ReportsDtos.DoctorProductivityResponse;
import edu.unimag.domine.api.dto.ReportsDtos.NoShowPatientResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReportsService {
    List<OfficeOccupancyResponse> getOfficeOccupancy(
            LocalDate date,
            LocalTime startAt,
            LocalTime endAt
    );

    List<DoctorProductivityResponse> getDoctorProductivity();

    List<NoShowPatientResponse> getNoShowPatients(
            LocalDate date,
            LocalTime startAt,
            LocalTime endAt
    );
}
