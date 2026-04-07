package edu.unimag.domine.service;

import edu.unimag.domine.api.dto.OfficeDtos.CreateOfficeRequest;
import edu.unimag.domine.api.dto.OfficeDtos.OfficeResponse;
import edu.unimag.domine.api.dto.OfficeDtos.UpdateOfficeRequest;
import edu.unimag.domine.entities.Office;
import edu.unimag.domine.exceptions.ResourceNotFoundException;
import edu.unimag.domine.exceptions.ValidationException;
import edu.unimag.domine.mappers.OfficeMapper;
import edu.unimag.domine.repositories.OfficeRepository;
import edu.unimag.domine.service.implementation.OfficeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfficeServiceImplTest {

    @Mock private OfficeRepository repository;
    @Mock private OfficeMapper mapper;

    @InjectMocks private OfficeServiceImpl service;

    private final UUID id = UUID.randomUUID();

    @Test
    void shouldCreateSuccessfully() {
        var request = new CreateOfficeRequest("Consultorio 101", "Piso 1");
        var entity = new Office();
        var response = new OfficeResponse(id, "Consultorio 101", "Piso 1", true);

        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        var result = service.create(request);

        assertThat(result.name()).isEqualTo("Consultorio 101");
        verify(repository).save(any());
    }

    @Test
    void shouldGetAll() {
        when(repository.findAll()).thenReturn(List.of(new Office()));
        when(mapper.toResponse(any())).thenReturn(new OfficeResponse(id, "Consultorio 101", "Piso 1", true));

        var result = service.getAll();
        assertThat(result).hasSize(1);
    }

    @Test
    void shouldUpdateSuccessfully() {
        var request = new UpdateOfficeRequest("Consultorio 102", "Piso 1", false);
        var entity = new Office();
        var response = new OfficeResponse(id, "Consultorio 102", "Piso 1", false);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        var result = service.update(id, request);

        assertThat(result.name()).isEqualTo("Consultorio 102");
        assertThat(result.active()).isFalse();
        verify(mapper).update(request, entity);
    }
}