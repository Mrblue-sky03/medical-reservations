package edu.unimag.domine.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import edu.unimag.domine.api.controller.AppointmentController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.unimag.domine.api.dto.AppointmentDtos.AppointmentResponse;
import edu.unimag.domine.api.dto.AppointmentDtos.CancelAppointmentRequest;
import edu.unimag.domine.api.dto.AppointmentDtos.CreateAppointmentRequest;
import edu.unimag.domine.entities.enums.Status;
import edu.unimag.domine.exceptions.ResourceNotFoundException;
import edu.unimag.domine.service.AppointmentService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentService appointmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID appointmentId;
    private CreateAppointmentRequest createRequest;
    private AppointmentResponse appointmentResponse;

    @BeforeEach
    void setUp() {
        appointmentId = UUID.randomUUID();
        createRequest = new CreateAppointmentRequest(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            LocalDate.now().plusDays(1),
            LocalTime.of(10, 0),
            null
        );
        appointmentResponse = new AppointmentResponse(
            appointmentId,
            createRequest.patientId(),
            createRequest.doctorId(),
            createRequest.officeId(),
            createRequest.appointmentTypeId(),
            createRequest.date(),
            createRequest.startsAt(),
            createRequest.startsAt().plusMinutes(30),
            Status.SCHEDULED,
            null
        );
    }

    @Test
    void testCreateAppointmentSuccess() throws Exception {
        when(appointmentService.create(any(CreateAppointmentRequest.class))).thenReturn(appointmentResponse);

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").value(appointmentId.toString()))
            .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    void testGetAppointmentByIdSuccess() throws Exception {
        when(appointmentService.getById(appointmentId)).thenReturn(appointmentResponse);

        mockMvc.perform(get("/api/appointments/{id}", appointmentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(appointmentId.toString()))
            .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    void testGetAppointmentByIdNotFound() throws Exception {
        when(appointmentService.getById(appointmentId))
            .thenThrow(new ResourceNotFoundException("Appointment not found"));

        mockMvc.perform(get("/api/appointments/{id}", appointmentId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllAppointmentsSuccess() throws Exception {
        when(appointmentService.getAll()).thenReturn(List.of(appointmentResponse));

        mockMvc.perform(get("/api/appointments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(appointmentId.toString()))
            .andExpect(jsonPath("$[0].status").value("SCHEDULED"));
    }

    @Test
    void testConfirmAppointmentSuccess() throws Exception {
        var confirmed = new AppointmentResponse(
            appointmentId, createRequest.patientId(), createRequest.doctorId(),
            createRequest.officeId(), createRequest.appointmentTypeId(),
            createRequest.date(), createRequest.startsAt(), createRequest.startsAt().plusMinutes(30),
            Status.CONFIRMED, null
        );
        when(appointmentService.confirm(appointmentId)).thenReturn(confirmed);

        mockMvc.perform(patch("/api/appointments/{id}/confirm", appointmentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void testConfirmAppointmentNotFound() throws Exception {
        when(appointmentService.confirm(appointmentId))
            .thenThrow(new ResourceNotFoundException("Appointment not found"));

        mockMvc.perform(patch("/api/appointments/{id}/confirm", appointmentId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testCompleteAppointmentSuccess() throws Exception {
        var completed = new AppointmentResponse(
            appointmentId, createRequest.patientId(), createRequest.doctorId(),
            createRequest.officeId(), createRequest.appointmentTypeId(),
            createRequest.date(), createRequest.startsAt(), createRequest.startsAt().plusMinutes(30),
            Status.COMPLETED, null
        );
        when(appointmentService.complete(appointmentId)).thenReturn(completed);

        mockMvc.perform(patch("/api/appointments/{id}/complete", appointmentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void testCompleteAppointmentNotFound() throws Exception {
        when(appointmentService.complete(appointmentId))
            .thenThrow(new ResourceNotFoundException("Appointment not found"));

        mockMvc.perform(patch("/api/appointments/{id}/complete", appointmentId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testMarkAsNoShowSuccess() throws Exception {
        var noShow = new AppointmentResponse(
            appointmentId, createRequest.patientId(), createRequest.doctorId(),
            createRequest.officeId(), createRequest.appointmentTypeId(),
            createRequest.date(), createRequest.startsAt(), createRequest.startsAt().plusMinutes(30),
            Status.NO_SHOW, null
        );
        when(appointmentService.markAsNoShow(appointmentId)).thenReturn(noShow);

        mockMvc.perform(patch("/api/appointments/{id}/no-show", appointmentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("NO_SHOW"));
    }

    @Test
    void testMarkAsNoShowNotFound() throws Exception {
        when(appointmentService.markAsNoShow(appointmentId))
            .thenThrow(new ResourceNotFoundException("Appointment not found"));

        mockMvc.perform(patch("/api/appointments/{id}/no-show", appointmentId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testCancelAppointmentSuccess() throws Exception {
        var cancelRequest = new CancelAppointmentRequest("Patient requested cancellation");
        var cancelled = new AppointmentResponse(
            appointmentId, createRequest.patientId(), createRequest.doctorId(),
            createRequest.officeId(), createRequest.appointmentTypeId(),
            createRequest.date(), createRequest.startsAt(), createRequest.startsAt().plusMinutes(30),
            Status.CANCELLED, null
        );
        when(appointmentService.cancel(eq(appointmentId), any(CancelAppointmentRequest.class))).thenReturn(cancelled);

        mockMvc.perform(patch("/api/appointments/{id}/cancel", appointmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void testCancelAppointmentNotFound() throws Exception {
        var cancelRequest = new CancelAppointmentRequest("Patient requested cancellation");
        when(appointmentService.cancel(eq(appointmentId), any(CancelAppointmentRequest.class)))
            .thenThrow(new ResourceNotFoundException("Appointment not found"));

        mockMvc.perform(patch("/api/appointments/{id}/cancel", appointmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelRequest)))
            .andExpect(status().isNotFound());
    }
}
