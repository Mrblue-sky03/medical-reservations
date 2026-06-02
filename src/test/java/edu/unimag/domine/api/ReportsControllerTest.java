package edu.unimag.domine.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import edu.unimag.domine.api.controller.ReportsController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.unimag.domine.api.dto.ReportsDtos.DoctorProductivityResponse;
import edu.unimag.domine.api.dto.ReportsDtos.NoShowPatientResponse;
import edu.unimag.domine.api.dto.ReportsDtos.OfficeOccupancyResponse;
import edu.unimag.domine.service.ReportsService;

@WebMvcTest(ReportsController.class)
class ReportsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportsService reportService;

    private UUID officeId;
    private UUID doctorId;
    private UUID patientId;

    @BeforeEach
    void setUp() {
        officeId = UUID.randomUUID();
        doctorId = UUID.randomUUID();
        patientId = UUID.randomUUID();
    }

    @Test
    void testGetOfficeOccupancySuccess() throws Exception {
        var occupancy = List.of(
            new OfficeOccupancyResponse(officeId, "Consultorio 101", 15L),
            new OfficeOccupancyResponse(UUID.randomUUID(), "Consultorio 202", 8L)
        );
        when(reportService.getOfficeOccupancy(any(), any(), any())).thenReturn(occupancy);

        mockMvc.perform(get("/api/reports/office-occupancy")
                .param("date", "2025-06-01")
                .param("startAt", "08:00:00")
                .param("endAt", "18:00:00"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].officeId").value(officeId.toString()))
            .andExpect(jsonPath("$[0].name").value("Consultorio 101"))
            .andExpect(jsonPath("$[0].totalAppointments").value(15));
    }

    @Test
    void testGetOfficeOccupancyWithoutParamsUsesDefaults() throws Exception {
        when(reportService.getOfficeOccupancy(any(), any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/reports/office-occupancy"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetNoShowPatientsSuccess() throws Exception {
        var noShows = List.of(
            new NoShowPatientResponse(patientId, "Carlos Rodríguez", 3L)
        );
        when(reportService.getNoShowPatients(any(), any(), any())).thenReturn(noShows);

        mockMvc.perform(get("/api/reports/no-show-patients")
                .param("date", "2025-06-01")
                .param("startAt", "08:00:00")
                .param("endAt", "18:00:00"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].patientId").value(patientId.toString()))
            .andExpect(jsonPath("$[0].fullName").value("Carlos Rodríguez"))
            .andExpect(jsonPath("$[0].noShowCount").value(3));
    }

    @Test
    void testGetNoShowPatientsWithoutParamsUsesDefaults() throws Exception {
        when(reportService.getNoShowPatients(any(), any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/reports/no-show-patients"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetDoctorProductivitySuccess() throws Exception {
        var productivity = List.of(
            new DoctorProductivityResponse(doctorId, "Dr. Ana Martínez", 42L),
            new DoctorProductivityResponse(UUID.randomUUID(), "Dr. Luis Torres", 35L)
        );
        when(reportService.getDoctorProductivity()).thenReturn(productivity);

        mockMvc.perform(get("/api/reports/doctor-productivity"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].doctorId").value(doctorId.toString()))
            .andExpect(jsonPath("$[0].fullName").value("Dr. Ana Martínez"))
            .andExpect(jsonPath("$[0].completedAppointments").value(42));
    }

    @Test
    void testGetDoctorProductivityEmpty() throws Exception {
        when(reportService.getDoctorProductivity()).thenReturn(List.of());

        mockMvc.perform(get("/api/reports/doctor-productivity"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }
}
