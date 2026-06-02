package edu.unimag.domine.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import edu.unimag.domine.api.controller.DoctorController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.unimag.domine.api.dto.DoctorDtos.CreateDoctorRequest;
import edu.unimag.domine.api.dto.DoctorDtos.DoctorResponse;
import edu.unimag.domine.api.dto.DoctorDtos.UpdateDoctorRequest;
import edu.unimag.domine.entities.enums.DocumentType;
import edu.unimag.domine.exceptions.ResourceNotFoundException;
import edu.unimag.domine.service.DoctorService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(DoctorController.class)
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorService doctorService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID doctorId;
    private UUID specialtyId;
    private CreateDoctorRequest createRequest;
    private DoctorResponse doctorResponse;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();
        specialtyId = UUID.randomUUID();
        createRequest = new CreateDoctorRequest(
            specialtyId,
            "Dr. Juan Pérez",
            "juan.perez@hospital.com",
            "3001234567",
            "CC123456",
            DocumentType.CC,
            "LIC-2024-001"
        );
        doctorResponse = new DoctorResponse(
            doctorId,
            specialtyId,
            "Dr. Juan Pérez",
            "juan.perez@hospital.com",
            "3001234567",
            "CC123456",
            DocumentType.CC,
            "LIC-2024-001",
            true
        );
    }

    @Test
    void testCreateDoctorSuccess() throws Exception {
        when(doctorService.create(any(CreateDoctorRequest.class))).thenReturn(doctorResponse);

        mockMvc.perform(post("/api/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").value(doctorId.toString()))
            .andExpect(jsonPath("$.fullName").value("Dr. Juan Pérez"))
            .andExpect(jsonPath("$.email").value("juan.perez@hospital.com"));
    }

    @Test
    void testCreateDoctorWithInvalidRequest() throws Exception {
        var invalidRequest = new CreateDoctorRequest(
            null,
            "",
            "not-a-valid-email",
            "",
            "",
            null,
            ""
        );

        mockMvc.perform(post("/api/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetDoctorByIdSuccess() throws Exception {
        when(doctorService.getDoctorById(doctorId)).thenReturn(doctorResponse);

        mockMvc.perform(get("/api/doctors/{id}", doctorId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(doctorId.toString()))
            .andExpect(jsonPath("$.fullName").value("Dr. Juan Pérez"));
    }

    @Test
    void testGetDoctorByIdNotFound() throws Exception {
        when(doctorService.getDoctorById(doctorId))
            .thenThrow(new ResourceNotFoundException("Doctor not found"));

        mockMvc.perform(get("/api/doctors/{id}", doctorId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllDoctorsSuccess() throws Exception {
        when(doctorService.getAllDoctors()).thenReturn(List.of(doctorResponse));

        mockMvc.perform(get("/api/doctors"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(doctorId.toString()))
            .andExpect(jsonPath("$[0].fullName").value("Dr. Juan Pérez"));
    }

    @Test
    void testUpdateDoctorSuccess() throws Exception {
        var updateRequest = new UpdateDoctorRequest(
            specialtyId,
            "Dr. Juan Pérez Actualizado",
            "juan.updated@hospital.com",
            "3009876543",
            "LIC-2024-001",
            "CC123456",
            true
        );
        var updated = new DoctorResponse(
            doctorId, specialtyId,
            "Dr. Juan Pérez Actualizado",
            "juan.updated@hospital.com",
            "3009876543", "CC123456", DocumentType.CC, "LIC-2024-001", true
        );
        when(doctorService.updateDoctor(eq(doctorId), any(UpdateDoctorRequest.class))).thenReturn(updated);

        mockMvc.perform(patch("/api/doctors/{id}", doctorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fullName").value("Dr. Juan Pérez Actualizado"))
            .andExpect(jsonPath("$.email").value("juan.updated@hospital.com"));
    }

    @Test
    void testUpdateDoctorNotFound() throws Exception {
        var updateRequest = new UpdateDoctorRequest(
            specialtyId, "Dr. Juan", "juan@hospital.com",
            "3001234567", "LIC-001", "CC123456", true
        );
        when(doctorService.updateDoctor(eq(doctorId), any(UpdateDoctorRequest.class)))
            .thenThrow(new ResourceNotFoundException("Doctor not found"));

        mockMvc.perform(patch("/api/doctors/{id}", doctorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateDoctorWithInvalidRequest() throws Exception {
        var invalidRequest = new UpdateDoctorRequest(
            null, "", "not-an-email", "", "", "", null
        );

        mockMvc.perform(patch("/api/doctors/{id}", doctorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }
}
