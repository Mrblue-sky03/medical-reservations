package edu.unimag.domine.service;

import edu.unimag.domine.api.dto.SpecialtyDtos.CreateSpecialtyRequest;
import edu.unimag.domine.api.dto.SpecialtyDtos.UpdateSpecialtyRequest;
import edu.unimag.domine.api.dto.SpecialtyDtos.SpecialtyResponse;

import java.util.List;
import java.util.UUID;

public interface SpecialtyService {
    SpecialtyResponse create(CreateSpecialtyRequest req);
    SpecialtyResponse getById(UUID id);
    List<SpecialtyResponse> getAll();
    SpecialtyResponse update(UUID id, UpdateSpecialtyRequest req); 
    void delete(UUID id);                                         
}