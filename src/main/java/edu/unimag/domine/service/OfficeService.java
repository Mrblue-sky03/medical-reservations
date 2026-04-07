package edu.unimag.domine.service;

import edu.unimag.domine.api.dto.OfficeDtos.UpdateOfficeRequest;
import edu.unimag.domine.api.dto.OfficeDtos.OfficeResponse;
import edu.unimag.domine.api.dto.OfficeDtos.CreateOfficeRequest;

import java.util.List;
import java.util.UUID;

public interface OfficeService {

    OfficeResponse create(CreateOfficeRequest req);
    List<OfficeResponse> getAll();
    OfficeResponse update(UUID id, UpdateOfficeRequest request);
}
