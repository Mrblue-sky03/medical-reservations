package edu.unimag.domine.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import edu.unimag.domine.api.controller.AppointmentTypeController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.unimag.domine.api.dto.AppointmentTypesDtos.AppointmentTypeResponse;
import edu.unimag.domine.api.dto.AppointmentTypesDtos.CreateAppointmentTypeRequest;
import edu.unimag.domine.api.dto.AppointmentTypesDtos.UpdateAppointmentTypeRequest;
import edu.unimag.domine.exceptions.ResourceNotFoundException;
import edu.unimag.domine.service.AppointmentTypeService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(AppointmentTypeController.class)
class AppointmentTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentTypeService appointmentTypeService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID typeId;
    private CreateAppointmentTypeRequest createRequest;
    private AppointmentTypeResponse typeResponse;

    @BeforeEach
    void setUp() {
        typeId = UUID.randomUUID();
        createRequest = new CreateAppointmentTypeRequest("Consulta General", "Consulta médica general", 30);
        typeResponse = new AppointmentTypeResponse(typeId, "Consulta General", "Consulta médica general", 30);
    }

    @Test
    void testCreateAppointmentTypeSuccess() throws Exception {
        when(appointmentTypeService.create(any(CreateAppointmentTypeRequest.class))).thenReturn(typeResponse);

        mockMvc.perform(post("/api/appointment-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").value(typeId.toString()))
            .andExpect(jsonPath("$.name").value("Consulta General"))
            .andExpect(jsonPath("$.durationMinutes").value(30));
    }

    @Test
    void testCreateAppointmentTypeWithInvalidRequest() throws Exception {
        var invalidRequest = new CreateAppointmentTypeRequest("", null, -10);

        mockMvc.perform(post("/api/appointment-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllAppointmentTypesSuccess() throws Exception {
        when(appointmentTypeService.getAll()).thenReturn(List.of(typeResponse));

        mockMvc.perform(get("/api/appointment-types"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(typeId.toString()))
            .andExpect(jsonPath("$[0].name").value("Consulta General"));
    }

    @Test
    void testGetAppointmentTypeByIdSuccess() throws Exception {
        when(appointmentTypeService.getById(typeId)).thenReturn(typeResponse);

        mockMvc.perform(get("/api/appointment-types/{id}", typeId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(typeId.toString()))
            .andExpect(jsonPath("$.name").value("Consulta General"))
            .andExpect(jsonPath("$.durationMinutes").value(30));
    }

    @Test
    void testGetAppointmentTypeByIdNotFound() throws Exception {
        when(appointmentTypeService.getById(typeId))
            .thenThrow(new ResourceNotFoundException("Appointment type not found"));

        mockMvc.perform(get("/api/appointment-types/{id}", typeId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateAppointmentTypeSuccess() throws Exception {
        var updateRequest = new UpdateAppointmentTypeRequest("Consulta Especializada", "Consulta con especialista", 45);
        var updated = new AppointmentTypeResponse(typeId, "Consulta Especializada", "Consulta con especialista", 45);
        when(appointmentTypeService.update(eq(typeId), any(UpdateAppointmentTypeRequest.class))).thenReturn(updated);

        mockMvc.perform(patch("/api/appointment-types/{id}", typeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Consulta Especializada"))
            .andExpect(jsonPath("$.durationMinutes").value(45));
    }

    @Test
    void testUpdateAppointmentTypeNotFound() throws Exception {
        var updateRequest = new UpdateAppointmentTypeRequest("Consulta", "Descripción", 30);
        when(appointmentTypeService.update(eq(typeId), any(UpdateAppointmentTypeRequest.class)))
            .thenThrow(new ResourceNotFoundException("Appointment type not found"));

        mockMvc.perform(patch("/api/appointment-types/{id}", typeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteAppointmentTypeSuccess() throws Exception {
        doNothing().when(appointmentTypeService).delete(typeId);

        mockMvc.perform(delete("/api/appointment-types/{id}", typeId))
            .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteAppointmentTypeNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Appointment type not found"))
            .when(appointmentTypeService).delete(typeId);

        mockMvc.perform(delete("/api/appointment-types/{id}", typeId))
            .andExpect(status().isNotFound());
    }
}
