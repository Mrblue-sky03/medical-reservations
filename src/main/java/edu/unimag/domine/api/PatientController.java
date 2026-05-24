package edu.unimag.domine.api;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import edu.unimag.domine.api.dto.PatientDtos.CreatePatientRequest;
import edu.unimag.domine.api.dto.PatientDtos.PatientResponse;
import edu.unimag.domine.api.dto.PatientDtos.UpdatePatientRequest;
import edu.unimag.domine.service.PatientService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Validated

public class PatientController {
     
    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientResponse> create(@RequestBody @Valid CreatePatientRequest request, UriComponentsBuilder uriBuilder) {
        var patientCreated = patientService.create(request);
        var location = uriBuilder.path("/api/patients/{id}").buildAndExpand(patientCreated.id()).toUri();
        return ResponseEntity.created(location).body(patientCreated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getById(@PathVariable UUID id) {
        var patient = patientService.getById(id);
        return ResponseEntity.ok(patient);
    }

    @GetMapping
    public ResponseEntity<Page<PatientResponse>> getAll(Pageable pageable){
        var patients = patientService.getAll(pageable);
        return ResponseEntity.ok(patients);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PatientResponse> update(UpdatePatientRequest request, @PathVariable UUID id){
        var patientUpdated = patientService.update(id, request);
        return ResponseEntity.ok(patientUpdated);
    }
    
}
