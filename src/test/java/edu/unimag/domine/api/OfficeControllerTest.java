package edu.unimag.domine.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import edu.unimag.domine.api.controller.OfficeController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.unimag.domine.api.dto.OfficeDtos.CreateOfficeRequest;
import edu.unimag.domine.api.dto.OfficeDtos.OfficeResponse;
import edu.unimag.domine.api.dto.OfficeDtos.UpdateOfficeRequest;
import edu.unimag.domine.exceptions.ResourceNotFoundException;
import edu.unimag.domine.service.OfficeService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(OfficeController.class)
class OfficeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OfficeService officeService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID officeId;
    private CreateOfficeRequest createRequest;
    private OfficeResponse officeResponse;

    @BeforeEach
    void setUp() {
        officeId = UUID.randomUUID();
        createRequest = new CreateOfficeRequest("Consultorio 101", "Piso 1, Ala Norte");
        officeResponse = new OfficeResponse(officeId, "Consultorio 101", "Piso 1, Ala Norte", true);
    }

    @Test
    void testCreateOfficeSuccess() throws Exception {
        when(officeService.create(any(CreateOfficeRequest.class))).thenReturn(officeResponse);

        mockMvc.perform(post("/api/offices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").value(officeId.toString()))
            .andExpect(jsonPath("$.name").value("Consultorio 101"))
            .andExpect(jsonPath("$.location").value("Piso 1, Ala Norte"));
    }

    @Test
    void testCreateOfficeWithInvalidRequest() throws Exception {
        var invalidRequest = new CreateOfficeRequest("", "");

        mockMvc.perform(post("/api/offices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllOfficesSuccess() throws Exception {
        when(officeService.getAll()).thenReturn(List.of(officeResponse));

        mockMvc.perform(get("/api/offices"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(officeId.toString()))
            .andExpect(jsonPath("$[0].name").value("Consultorio 101"));
    }

    @Test
    void testUpdateOfficeSuccess() throws Exception {
        var updateRequest = new UpdateOfficeRequest("Consultorio 202", "Piso 2, Ala Sur", true);
        var updated = new OfficeResponse(officeId, "Consultorio 202", "Piso 2, Ala Sur", true);
        when(officeService.update(eq(officeId), any(UpdateOfficeRequest.class))).thenReturn(updated);

        mockMvc.perform(patch("/api/offices/{id}", officeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Consultorio 202"))
            .andExpect(jsonPath("$.location").value("Piso 2, Ala Sur"));
    }

    @Test
    void testUpdateOfficeNotFound() throws Exception {
        var updateRequest = new UpdateOfficeRequest("Consultorio 202", "Piso 2", true);
        when(officeService.update(eq(officeId), any(UpdateOfficeRequest.class)))
            .thenThrow(new ResourceNotFoundException("Office not found"));

        mockMvc.perform(patch("/api/offices/{id}", officeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateOfficeWithInvalidRequest() throws Exception {
        var invalidRequest = new UpdateOfficeRequest("", "", null);

        mockMvc.perform(patch("/api/offices/{id}", officeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }
}
