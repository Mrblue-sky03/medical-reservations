package edu.unimag.domine.service;

import edu.unimag.domine.api.dto.SpecialtyDtos.CreateSpecialtyRequest;
import edu.unimag.domine.api.dto.SpecialtyDtos.SpecialtyResponse;
import edu.unimag.domine.entities.Specialty;
import edu.unimag.domine.mappers.SpecialtyMapper;
import edu.unimag.domine.repositories.SpecialtyRepository;
import edu.unimag.domine.service.implementation.SpecialtyServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialtyServiceImplTest {

    @Mock private SpecialtyRepository repository;
    @Mock private SpecialtyMapper mapper;

    @InjectMocks private SpecialtyServiceImpl service;

    private final UUID id = UUID.randomUUID();

    @Test
    void shouldCreateSuccessfully() {
        var request = new CreateSpecialtyRequest("Cardiología", "Especialidad del corazón");
        var entity = new Specialty();
        var response = new SpecialtyResponse(id, "Cardiología", "Especialidad del corazón");

        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        var result = service.create(request);

        assertThat(result.name()).isEqualTo("Cardiología");
        verify(repository).save(any());
    }

    @Test
    void shouldGetByIdSuccessfully() {
        var entity = new Specialty();
        var response = new SpecialtyResponse(id, "Cardiología", "Especialidad del corazón");

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        var result = service.getById(id);

        assertThat(result).isNotNull();
    }

    @Test
    void shouldGetAll() {
        when(repository.findAll()).thenReturn(List.of(new Specialty()));
        when(mapper.toResponse(any())).thenReturn(new SpecialtyResponse(id, "Cardiología", "Desc"));

        var result = service.getAll();
        assertThat(result).hasSize(1);
    }
}