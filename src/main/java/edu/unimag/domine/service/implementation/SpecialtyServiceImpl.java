package edu.unimag.domine.service.implementation;

import edu.unimag.domine.api.dto.SpecialtyDtos.CreateSpecialtyRequest;
import edu.unimag.domine.api.dto.SpecialtyDtos.SpecialtyResponse;
import edu.unimag.domine.entities.Specialty;
import edu.unimag.domine.exceptions.ResourceNotFoundException;
import edu.unimag.domine.exceptions.ValidationException;
import edu.unimag.domine.mappers.SpecialtyMapper;
import edu.unimag.domine.repositories.SpecialtyRepository;
import edu.unimag.domine.service.SpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    @Override
    public SpecialtyResponse create(CreateSpecialtyRequest req) {
        if (Objects.isNull(req)) {
            throw new ValidationException("request can not be null");
        }
        Specialty specialty = specialtyMapper.toEntity(req);
        return specialtyMapper.toResponse(specialtyRepository.save(specialty));
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialtyResponse getById(UUID id) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("specialty not found: " + id));
        return specialtyMapper.toResponse(specialty);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyResponse> getAll() {
        return specialtyRepository.findAll().stream()
                .map(specialtyMapper::toResponse)
                .toList();
    }
}