package edu.unimag.domine.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import edu.unimag.domine.api.dto.DoctorDtos.CreateDoctorRequest;
import edu.unimag.domine.api.dto.DoctorDtos.DoctorResponse;
import edu.unimag.domine.api.dto.DoctorDtos.UpdateDoctorRequest;
import edu.unimag.domine.service.DoctorService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/doctors")

public class DoctorController {

    private final DoctorService doctorService;
    
    @PostMapping
    public ResponseEntity<DoctorResponse> create(@RequestBody @Valid CreateDoctorRequest request, UriComponentsBuilder uriBuilder){
        var doctorCreated = doctorService.create(request);
        var location = uriBuilder.path("/api/doctors/{id}").buildAndExpand(doctorCreated.id()).toUri();
        return ResponseEntity.created(location).body(doctorCreated);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getById(@PathVariable UUID id) {
        var doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(doctor);
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getAll() {
        var doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DoctorResponse> update(@RequestBody @Valid UpdateDoctorRequest request, @PathVariable UUID id) {
        var doctorUpdated = doctorService.updateDoctor(id, request);
        return ResponseEntity.ok(doctorUpdated);
    }
}
