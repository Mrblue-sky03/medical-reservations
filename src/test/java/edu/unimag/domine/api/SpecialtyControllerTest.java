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

import edu.unimag.domine.api.controller.SpecialtyController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.unimag.domine.api.dto.SpecialtyDtos.CreateSpecialtyRequest;
import edu.unimag.domine.api.dto.SpecialtyDtos.SpecialtyResponse;
import edu.unimag.domine.api.dto.SpecialtyDtos.UpdateSpecialtyRequest;
import edu.unimag.domine.exceptions.ResourceNotFoundException;
import edu.unimag.domine.service.SpecialtyService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(SpecialtyController.class)
class SpecialtyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SpecialtyService specialtyService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID specialtyId;
    private CreateSpecialtyRequest createRequest;
    private SpecialtyResponse specialtyResponse;

    @BeforeEach
    void setUp() {
        specialtyId = UUID.randomUUID();
        createRequest = new CreateSpecialtyRequest("Cardiología", "Especialidad del corazón");
        specialtyResponse = new SpecialtyResponse(specialtyId, "Cardiología", "Especialidad del corazón");
    }

    @Test
    void testCreateSpecialtySuccess() throws Exception {
        when(specialtyService.create(any(CreateSpecialtyRequest.class))).thenReturn(specialtyResponse);

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").value(specialtyId.toString()))
            .andExpect(jsonPath("$.name").value("Cardiología"));
    }

    @Test
    void testCreateSpecialtyWithInvalidRequest() throws Exception {
        var invalidRequest = new CreateSpecialtyRequest("", null);

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllSpecialtiesSuccess() throws Exception {
        when(specialtyService.getAll()).thenReturn(List.of(specialtyResponse));

        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(specialtyId.toString()))
            .andExpect(jsonPath("$[0].name").value("Cardiología"));
    }

    @Test
    void testGetSpecialtyByIdSuccess() throws Exception {
        when(specialtyService.getById(specialtyId)).thenReturn(specialtyResponse);

        mockMvc.perform(get("/api/specialties/{id}", specialtyId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(specialtyId.toString()))
            .andExpect(jsonPath("$.name").value("Cardiología"))
            .andExpect(jsonPath("$.description").value("Especialidad del corazón"));
    }

    @Test
    void testGetSpecialtyByIdNotFound() throws Exception {
        when(specialtyService.getById(specialtyId))
            .thenThrow(new ResourceNotFoundException("Specialty not found"));

        mockMvc.perform(get("/api/specialties/{id}", specialtyId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateSpecialtySuccess() throws Exception {
        var updateRequest = new UpdateSpecialtyRequest("Cardiología Intervencionista", "Procedimientos cardíacos");
        var updated = new SpecialtyResponse(specialtyId, "Cardiología Intervencionista", "Procedimientos cardíacos");
        when(specialtyService.update(eq(specialtyId), any(UpdateSpecialtyRequest.class))).thenReturn(updated);

        mockMvc.perform(patch("/api/specialties/{id}", specialtyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Cardiología Intervencionista"));
    }

    @Test
    void testUpdateSpecialtyNotFound() throws Exception {
        var updateRequest = new UpdateSpecialtyRequest("Cardiología", "Descripción");
        when(specialtyService.update(eq(specialtyId), any(UpdateSpecialtyRequest.class)))
            .thenThrow(new ResourceNotFoundException("Specialty not found"));

        mockMvc.perform(patch("/api/specialties/{id}", specialtyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteSpecialtySuccess() throws Exception {
        doNothing().when(specialtyService).delete(specialtyId);

        mockMvc.perform(delete("/api/specialties/{id}", specialtyId))
            .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteSpecialtyNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Specialty not found"))
            .when(specialtyService).delete(specialtyId);

        mockMvc.perform(delete("/api/specialties/{id}", specialtyId))
            .andExpect(status().isNotFound());
    }
}
