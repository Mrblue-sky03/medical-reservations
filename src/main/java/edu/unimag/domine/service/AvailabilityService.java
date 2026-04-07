package edu.unimag.domine.service;

import edu.unimag.domine.api.dto.AvailabilityDto.AvailabilitySlotResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AvailabilityService {
    List<AvailabilitySlotResponse> getAvailableSlots(UUID doctorId, UUID officeId, LocalDate date);
}
