package edu.unimag.domine.service.implementation;

import edu.unimag.domine.api.dto.OfficeDtos.CreateOfficeRequest;
import edu.unimag.domine.api.dto.OfficeDtos.OfficeResponse;
import edu.unimag.domine.api.dto.OfficeDtos.UpdateOfficeRequest;
import edu.unimag.domine.entities.Office;
import edu.unimag.domine.exceptions.ResourceNotFoundException;
import edu.unimag.domine.exceptions.ValidationException;
import edu.unimag.domine.mappers.OfficeMapper;
import edu.unimag.domine.repositories.OfficeRepository;
import edu.unimag.domine.service.OfficeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OfficeServiceImpl implements OfficeService {

    private final OfficeRepository officeRepository;
    private final OfficeMapper officeMapper;

    @Override
    public OfficeResponse create(CreateOfficeRequest req) {
        if (Objects.isNull(req)) {
            throw new ValidationException("request can not be null");
        }
        Office office = officeMapper.toEntity(req);
        return officeMapper.toResponse(officeRepository.save(office));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfficeResponse> getAll() {
        return officeRepository.findAll().stream()
                .map(officeMapper::toResponse)
                .toList();
    }

    @Override
    public OfficeResponse update(UUID id, UpdateOfficeRequest request) {
        if (Objects.isNull(request)) {
            throw new ValidationException("updated request can not be null");
        }

        Office office = officeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("office not found " + id));

        officeMapper.update(request, office);
        return officeMapper.toResponse(officeRepository.save(office));
    }
}