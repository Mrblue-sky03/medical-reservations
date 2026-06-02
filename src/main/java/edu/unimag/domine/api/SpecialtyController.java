package edu.unimag.domine.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import edu.unimag.domine.api.dto.SpecialtyDtos.CreateSpecialtyRequest;
import edu.unimag.domine.api.dto.SpecialtyDtos.UpdateSpecialtyRequest;
import edu.unimag.domine.api.dto.SpecialtyDtos.SpecialtyResponse;
import edu.unimag.domine.service.SpecialtyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
@Validated
public class SpecialtyController {
    
    private final SpecialtyService specialtyService;

    @PostMapping
    public ResponseEntity<SpecialtyResponse> create(@RequestBody @Valid CreateSpecialtyRequest request, UriComponentsBuilder uriBuilder){
        var specialtyCreated = specialtyService.create(request);
        var location = uriBuilder.path("/api/specialties/{id}").buildAndExpand(specialtyCreated.id()).toUri();
        return ResponseEntity.created(location).body(specialtyCreated);
    }

    @GetMapping
    public ResponseEntity<List<SpecialtyResponse>> getAll() {
        var specialties = specialtyService.getAll();
        return ResponseEntity.ok(specialties);
    }

    @GetMapping("/{id}")
    // 🛠️ Corrección: Se agregó @PathVariable para capturar correctamente el ID de la URL
    public ResponseEntity<SpecialtyResponse> getById(@PathVariable UUID id){
        var specialty = specialtyService.getById(id);
        return ResponseEntity.ok(specialty);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SpecialtyResponse> update(@PathVariable UUID id, @RequestBody @Valid UpdateSpecialtyRequest request) {
        var specialtyUpdated = specialtyService.update(id, request);
        return ResponseEntity.ok(specialtyUpdated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        specialtyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}