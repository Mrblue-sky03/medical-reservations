package edu.unimag.domine.api;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.unimag.domine.api.dto.ReportsDtos.DoctorProductivityResponse;
import edu.unimag.domine.api.dto.ReportsDtos.NoShowPatientResponse;
import edu.unimag.domine.api.dto.ReportsDtos.OfficeOccupancyResponse;
import edu.unimag.domine.service.ReportsService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")


public class ReportsController {

    private final ReportsService reportService;
   
    @GetMapping("/office-occupancy")
    public ResponseEntity<List<OfficeOccupancyResponse>> getOfficeOccupancy(
        @RequestParam LocalDate date,
        @RequestParam LocalTime startAt,
        @RequestParam LocalTime endAt) {

        var occupancy = reportService.getOfficeOccupancy(date, startAt, endAt);
        return ResponseEntity.ok(occupancy);
    }

    @GetMapping("/doctor-productivity")
    public ResponseEntity<List<DoctorProductivityResponse>> getDoctorProductivity(){
        var productivity = reportService.getDoctorProductivity();
        return ResponseEntity.ok(productivity);
    }

    @GetMapping("/no-show-patients")
    public ResponseEntity<List<NoShowPatientResponse>> getNoShowPatients(
            @RequestParam LocalDate date,
            @RequestParam(required = false) LocalTime startAt,
            @RequestParam(required = false) LocalTime endAt) {

        var noShows = reportService.getNoShowPatients(date, startAt, endAt);
        return ResponseEntity.ok(noShows);
    }
}


