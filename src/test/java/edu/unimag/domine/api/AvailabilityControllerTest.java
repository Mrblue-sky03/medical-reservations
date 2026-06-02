package edu.unimag.domine.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import edu.unimag.domine.api.controller.AvailabilityController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.unimag.domine.api.dto.AvailabilityDto.AvailabilitySlotResponse;
import edu.unimag.domine.service.AvailabilityService;

@WebMvcTest(AvailabilityController.class)
class AvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AvailabilityService availabilityService;

    private UUID doctorId;
    private UUID officeId;
    private UUID appointmentTypeId;
    private LocalDate date;
    private List<AvailabilitySlotResponse> slots;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();
        officeId = UUID.randomUUID();
        appointmentTypeId = UUID.randomUUID();
        date = LocalDate.now().plusDays(1);
        slots = List.of(
            new AvailabilitySlotResponse(date, LocalTime.of(10, 0), LocalTime.of(10, 30)),
            new AvailabilitySlotResponse(date, LocalTime.of(11, 0), LocalTime.of(11, 30))
        );
    }

    @Test
    void testGetDoctorAvailabilitySuccess() throws Exception {
        when(availabilityService.getAvailableSlots(
                eq(doctorId), eq(officeId), eq(date), eq(appointmentTypeId)))
            .thenReturn(slots);

        mockMvc.perform(get("/api/availability/doctors/{doctorId}", doctorId)
                .param("officeId", officeId.toString())
                .param("appointmentTypeId", appointmentTypeId.toString())
                .param("date", date.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").exists())
            .andExpect(jsonPath("$[1]").exists());
    }

    @Test
    void testGetDoctorAvailabilityWithoutOptionalParams() throws Exception {
        when(availabilityService.getAvailableSlots(
                eq(doctorId), isNull(), eq(date), isNull()))
            .thenReturn(slots);

        mockMvc.perform(get("/api/availability/doctors/{doctorId}", doctorId)
                .param("date", date.toString()))
            .andExpect(status().isOk());
    }

    @Test
    void testGetDoctorAvailabilityMissingRequiredDate() throws Exception {
        // El GlobalExceptionHandler captura MissingServletRequestParameterException
        // con el handler genérico de Exception, devolviendo 500
        mockMvc.perform(get("/api/availability/doctors/{doctorId}", doctorId)
                .param("officeId", officeId.toString()))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetDoctorAvailabilityReturnsEmptyList() throws Exception {
        when(availabilityService.getAvailableSlots(
                eq(doctorId), any(), eq(date), any()))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/availability/doctors/{doctorId}", doctorId)
                .param("date", date.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }
}
