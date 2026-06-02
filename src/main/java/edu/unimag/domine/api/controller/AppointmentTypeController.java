package edu.unimag.domine.api.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import edu.unimag.domine.api.dto.AppointmentTypesDtos.AppointmentTypeResponse;
import edu.unimag.domine.api.dto.AppointmentTypesDtos.CreateAppointmentTypeRequest;
import edu.unimag.domine.api.dto.AppointmentTypesDtos.UpdateAppointmentTypeRequest;
import edu.unimag.domine.service.AppointmentTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/appointment-types")
@Validated
public class AppointmentTypeController {

    private final AppointmentTypeService appointmentTypeService;

    @PostMapping
    public ResponseEntity<AppointmentTypeResponse> create(@RequestBody @Valid CreateAppointmentTypeRequest request, UriComponentsBuilder uriBuilder){
        var appointmentTypeCreated = appointmentTypeService.create(request);
        var location = uriBuilder.path("/api/appointment-types/{id}").buildAndExpand(appointmentTypeCreated.id()).toUri();
        return ResponseEntity.created(location).body(appointmentTypeCreated);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentTypeResponse>> getAll() {
        var appointmentTypes = appointmentTypeService.getAll();
        return ResponseEntity.ok(appointmentTypes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentTypeResponse> getById(@PathVariable UUID id) {
        var appointmentType = appointmentTypeService.getById(id);
        return ResponseEntity.ok(appointmentType);
    }    

    @PatchMapping("/{id}")
    public ResponseEntity<AppointmentTypeResponse> update(@PathVariable UUID id, @RequestBody @Valid UpdateAppointmentTypeRequest request) {
        var appointmentTypeUpdated = appointmentTypeService.update(id, request);
        return ResponseEntity.ok(appointmentTypeUpdated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        appointmentTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}