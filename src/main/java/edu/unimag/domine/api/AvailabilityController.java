package edu.unimag.domine.api;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.unimag.domine.api.dto.AvailabilityDto.AvailabilitySlotResponse;
import edu.unimag.domine.service.AvailabilityService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor

public class AvailabilityController {

    private final AvailabilityService availabilityService;
    
    @GetMapping("/doctors/{doctorId}")
    public ResponseEntity<List<AvailabilitySlotResponse>> getDoctorAvailability(@PathVariable UUID doctorId, @RequestParam UUID officeId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var availability = availabilityService.getAvailableSlots(doctorId, officeId, date);
        return ResponseEntity.ok(availability);
    }

}