package edu.unimag.domine.service;

import edu.unimag.domine.api.dto.SpecialtyDtos.SpecialtyResponse;
import edu.unimag.domine.api.dto.SpecialtyDtos.CreateSpecialtyRequest;

import java.util.List;
import java.util.UUID;

public interface SpecialtyService {
    SpecialtyResponse create(CreateSpecialtyRequest req);
    SpecialtyResponse getById(UUID id);
    List<SpecialtyResponse> getAll();
}
