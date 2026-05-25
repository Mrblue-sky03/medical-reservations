package edu.unimag.domine.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import edu.unimag.domine.api.dto.SpecialtyDtos.CreateSpecialtyRequest;
import edu.unimag.domine.api.dto.SpecialtyDtos.SpecialtyResponse;
import edu.unimag.domine.service.SpecialtyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/specialities")
@RequiredArgsConstructor
@Validated

public class SpecialtyController{
    
    private final SpecialtyService specialtyService;

    @PostMapping
    public ResponseEntity<SpecialtyResponse> create(@RequestBody @Valid CreateSpecialtyRequest request, UriComponentsBuilder uriBuilder){
        var specialtyCreated = specialtyService.create(request);
        var location = uriBuilder.path("/api/specialities/{id}").buildAndExpand(specialtyCreated.id()).toUri();
        return ResponseEntity.created(location).body(specialtyCreated);
    }

    @GetMapping
    public ResponseEntity<List<SpecialtyResponse>> getAll() {
        var specialties = specialtyService.getAll();
        return ResponseEntity.ok(specialties);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecialtyResponse> getById(UUID id){
        var specialty = specialtyService.getById(id);
        return ResponseEntity.ok(specialty);
    }

}