package edu.unimag.domine.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import edu.unimag.domine.api.dto.OfficeDtos.CreateOfficeRequest;
import edu.unimag.domine.api.dto.OfficeDtos.OfficeResponse;
import edu.unimag.domine.api.dto.OfficeDtos.UpdateOfficeRequest;
import edu.unimag.domine.service.OfficeService;
import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/offices")
@Validated

public class OfficeController {
    
    private final OfficeService officeService;

    @PostMapping
    public ResponseEntity<OfficeResponse> create(@RequestBody @Valid CreateOfficeRequest request, UriComponentsBuilder uriBuilder){
        var officeCreated = officeService.create(request);
        var location = uriBuilder.path("/api/offices/{id}").buildAndExpand(officeCreated.id()).toUri();
        return ResponseEntity.created(location).body(officeCreated);
    }

    @GetMapping
    public ResponseEntity<List<OfficeResponse>> getAll() {
        var offices = officeService.getAll();
        return ResponseEntity.ok(offices);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OfficeResponse> update(@RequestBody @Valid UpdateOfficeRequest request, @PathVariable UUID id) {
        var officeUpdated = officeService.update(id, request);
        return ResponseEntity.ok(officeUpdated);
    }
    
}   
