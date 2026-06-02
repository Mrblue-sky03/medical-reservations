package edu.unimag.domine.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import edu.unimag.domine.api.controller.DoctorSchedulesController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.unimag.domine.api.dto.DoctorScheduleDtos.CreateDoctorScheduleRequest;
import edu.unimag.domine.api.dto.DoctorScheduleDtos.DoctorScheduleResponse;
import edu.unimag.domine.entities.enums.DayOfWeek;
import edu.unimag.domine.exceptions.ResourceNotFoundException;
import edu.unimag.domine.service.DoctorScheduleService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(DoctorSchedulesController.class)
class DoctorSchedulesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorScheduleService doctorScheduleService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID doctorId;
    private UUID scheduleId;
    private CreateDoctorScheduleRequest createRequest;
    private DoctorScheduleResponse scheduleResponse;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();
        scheduleId = UUID.randomUUID();
        createRequest = new CreateDoctorScheduleRequest(
            doctorId,
            DayOfWeek.MONDAY,
            LocalTime.of(8, 0),
            LocalTime.of(17, 0)
        );
        scheduleResponse = new DoctorScheduleResponse(
            scheduleId,
            doctorId,
            DayOfWeek.MONDAY,
            LocalTime.of(8, 0),
            LocalTime.of(17, 0)
        );
    }

    @Test
    void testCreateScheduleSuccess() throws Exception {
        when(doctorScheduleService.create(eq(doctorId), any(CreateDoctorScheduleRequest.class)))
            .thenReturn(scheduleResponse);

        mockMvc.perform(post("/api/doctors/{doctorId}/schedules", doctorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").value(scheduleId.toString()))
            .andExpect(jsonPath("$.dayOfWeek").value("MONDAY"));
    }

    @Test
    void testCreateScheduleWithInvalidRequest() throws Exception {
        var invalidRequest = new CreateDoctorScheduleRequest(null, null, null, null);

        mockMvc.perform(post("/api/doctors/{doctorId}/schedules", doctorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllSchedulesSuccess() throws Exception {
        when(doctorScheduleService.getAllSchedules(doctorId)).thenReturn(List.of(scheduleResponse));

        mockMvc.perform(get("/api/doctors/{doctorId}/schedules", doctorId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(scheduleId.toString()))
            .andExpect(jsonPath("$[0].dayOfWeek").value("MONDAY"));
    }

    @Test
    void testUpdateScheduleSuccess() throws Exception {
        var updateRequest = new CreateDoctorScheduleRequest(
            doctorId, DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(18, 0)
        );
        var updated = new DoctorScheduleResponse(
            scheduleId, doctorId, DayOfWeek.TUESDAY,
            LocalTime.of(9, 0), LocalTime.of(18, 0)
        );
        when(doctorScheduleService.update(eq(scheduleId), any(CreateDoctorScheduleRequest.class)))
            .thenReturn(updated);

        mockMvc.perform(patch("/api/doctors/{doctorId}/schedules/{scheduleId}", doctorId, scheduleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dayOfWeek").value("TUESDAY"));
    }

    @Test
    void testUpdateScheduleNotFound() throws Exception {
        var updateRequest = new CreateDoctorScheduleRequest(
            doctorId, DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(18, 0)
        );
        when(doctorScheduleService.update(eq(scheduleId), any(CreateDoctorScheduleRequest.class)))
            .thenThrow(new ResourceNotFoundException("Schedule not found"));

        mockMvc.perform(patch("/api/doctors/{doctorId}/schedules/{scheduleId}", doctorId, scheduleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateScheduleWithInvalidRequest() throws Exception {
        var invalidRequest = new CreateDoctorScheduleRequest(null, null, null, null);

        mockMvc.perform(patch("/api/doctors/{doctorId}/schedules/{scheduleId}", doctorId, scheduleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteScheduleSuccess() throws Exception {
        doNothing().when(doctorScheduleService).delete(scheduleId);

        mockMvc.perform(delete("/api/doctors/{doctorId}/schedules/{scheduleId}", doctorId, scheduleId))
            .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteScheduleNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Schedule not found"))
            .when(doctorScheduleService).delete(scheduleId);

        mockMvc.perform(delete("/api/doctors/{doctorId}/schedules/{scheduleId}", doctorId, scheduleId))
            .andExpect(status().isNotFound());
    }
}
