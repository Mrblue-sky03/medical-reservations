package edu.unimag.domine.api;

import java.util.List;
import java.util.UUID;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import edu.unimag.domine.api.dto.AppointmentDtos.AppointmentResponse;
import edu.unimag.domine.api.dto.AppointmentDtos.CancelAppointmentRequest;
import edu.unimag.domine.api.dto.AppointmentDtos.CreateAppointmentRequest;
import edu.unimag.domine.service.AppointmentService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor

public class AppointmentController {

private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(@RequestBody CreateAppointmentRequest request, UriComponentsBuilder uriBuilder) {
        var response = appointmentService.create(request);
        var location = uriBuilder.path("/api/appointments/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getById(@PathVariable UUID id,UriComponentsBuilder uriBuilder) {
        var appointment = appointmentService.getById(id);
        return ResponseEntity.ok(appointment);
    }
    
    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getAll(UriComponentsBuilder uriBuilder) {
        var appointments = appointmentService.getAll();
        return ResponseEntity.ok(appointments);
    }
    
    @PatchMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponse> complete(UUID id, UriComponentsBuilder uriBuilder){
        var appointmentCompleted = appointmentService.complete(id);
        return ResponseEntity.ok(appointmentCompleted);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancel(UUID id, CancelAppointmentRequest request, UriComponentsBuilder uriBuilder){
        var appointmentCanceled = appointmentService.cancel(id, request);
        return ResponseEntity.ok(appointmentCanceled);
    }

    @PatchMapping("/{id}/no-show")
    public ResponseEntity<AppointmentResponse> markAsNoShow(UUID id, UriComponentsBuilder uriBuilder){
        var appointmentNoShow = appointmentService.markAsNoShow(id);
        return ResponseEntity.ok(appointmentNoShow);
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<AppointmentResponse> confirm(UUID id, UriComponentsBuilder uriBuilder){
        var appointmentConfirmed = appointmentService.confirm(id);
        return ResponseEntity.ok(appointmentConfirmed);
    }

}
