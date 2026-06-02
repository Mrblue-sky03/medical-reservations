package edu.unimag.domine.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.UUID;

import edu.unimag.domine.api.controller.PatientController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.unimag.domine.api.dto.PatientDtos.CreatePatientRequest;
import edu.unimag.domine.api.dto.PatientDtos.PatientResponse;
import edu.unimag.domine.api.dto.PatientDtos.UpdatePatientRequest;
import edu.unimag.domine.entities.enums.DocumentType;
import edu.unimag.domine.exceptions.ResourceNotFoundException;
import edu.unimag.domine.service.PatientService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService patientService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID patientId;
    private CreatePatientRequest createRequest;
    private PatientResponse patientResponse;

    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();
        createRequest = new CreatePatientRequest(
            "María García",
            DocumentType.CC,
            "987654321",
            "maria.garcia@email.com",
            "3107654321",
            LocalDate.of(1990, 5, 15)
        );
        patientResponse = new PatientResponse(
            patientId,
            "María García",
            DocumentType.CC,
            "987654321",
            "maria.garcia@email.com",
            "3107654321",
            true,
            LocalDate.of(1990, 5, 15)
        );
    }

    @Test
    void testCreatePatientSuccess() throws Exception {
        when(patientService.create(any(CreatePatientRequest.class))).thenReturn(patientResponse);

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").value(patientId.toString()))
            .andExpect(jsonPath("$.fullName").value("María García"))
            .andExpect(jsonPath("$.email").value("maria.garcia@email.com"));
    }

    @Test
    void testCreatePatientWithInvalidRequest() throws Exception {
        var invalidRequest = new CreatePatientRequest(
            "",
            null,
            "",
            "not-an-email",
            "",
            LocalDate.now().plusDays(1)
        );

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPatientByIdSuccess() throws Exception {
        when(patientService.getById(patientId)).thenReturn(patientResponse);

        mockMvc.perform(get("/api/patients/{id}", patientId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(patientId.toString()))
            .andExpect(jsonPath("$.fullName").value("María García"));
    }

    @Test
    void testGetPatientByIdNotFound() throws Exception {
        when(patientService.getById(patientId))
            .thenThrow(new ResourceNotFoundException("Patient not found"));

        mockMvc.perform(get("/api/patients/{id}", patientId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllPatientsSuccess() throws Exception {
        var page = new PageImpl<>(
            java.util.List.of(patientResponse),
            PageRequest.of(0, 10),
            1
        );
        when(patientService.getAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/patients")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(patientId.toString()))
            .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    void testUpdatePatientSuccess() throws Exception {
        var updateRequest = new UpdatePatientRequest(
            "María García Actualizada",
            "maria.updated@email.com",
            "3109999999",
            DocumentType.CC,
            true
        );
        var updated = new PatientResponse(
            patientId, "María García Actualizada", DocumentType.CC,
            "987654321", "maria.updated@email.com", "3109999999", true,
            LocalDate.of(1990, 5, 15)
        );
        when(patientService.update(eq(patientId), any(UpdatePatientRequest.class))).thenReturn(updated);

        mockMvc.perform(patch("/api/patients/{id}", patientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fullName").value("María García Actualizada"))
            .andExpect(jsonPath("$.email").value("maria.updated@email.com"));
    }

    @Test
    void testUpdatePatientNotFound() throws Exception {
        var updateRequest = new UpdatePatientRequest(
            "María García", "maria@email.com", "3107654321", DocumentType.CC, true
        );
        when(patientService.update(eq(patientId), any(UpdatePatientRequest.class)))
            .thenThrow(new ResourceNotFoundException("Patient not found"));

        mockMvc.perform(patch("/api/patients/{id}", patientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testUpdatePatientWithInvalidRequest() throws Exception {
        var invalidRequest = new UpdatePatientRequest(
            "", "not-an-email", "", null, null
        );

        mockMvc.perform(patch("/api/patients/{id}", patientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }
}
