package edu.unimag.domine.service;

import edu.unimag.domine.api.dto.AppointmentTypesDtos.AppointmentTypeResponse;
import edu.unimag.domine.api.dto.AppointmentTypesDtos.CreateAppointmentTypeRequest;
import edu.unimag.domine.entities.AppointmentType;
import edu.unimag.domine.exceptions.ConflictException;
import edu.unimag.domine.exceptions.ValidationException;
import edu.unimag.domine.mappers.AppointmentTypeMapper;
import edu.unimag.domine.repositories.AppointmentTypeRepository;
import edu.unimag.domine.service.implementation.AppointmentTypeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentTypeServiceImplTest {

    @Mock private AppointmentTypeRepository repository;
    @Mock private AppointmentTypeMapper mapper;

    @InjectMocks private AppointmentTypeServiceImpl service;

    @Test
    void shouldCreateSuccessfully() {
        // Usamos los campos correctos de tu DTO
        var request = new CreateAppointmentTypeRequest("General", "Consulta básica", 30);
        var entity = new AppointmentType();
        var response = new AppointmentTypeResponse(UUID.randomUUID(), "General", "Consulta básica", 30);

        when(repository.existsByName("General")).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        var result = service.create(request);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("General");
        assertThat(result.description()).isEqualTo("Consulta básica");
        verify(repository).save(any());
    }

    @Test
    void shouldThrowWhenRequestIsNull() {
        assertThrows(ValidationException.class, () -> service.create(null));
    }

    @Test
    void shouldThrowWhenNameExists() {
        var request = new CreateAppointmentTypeRequest("General", "Desc", 30);
        when(repository.existsByName("General")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.create(request));
        verify(repository, never()).save(any());
    }

    @Test
    void shouldGetAll() {
        var entity = new AppointmentType();
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(
                new AppointmentTypeResponse(UUID.randomUUID(), "General", "Desc", 30)
        );

        var result = service.getAll();

        assertThat(result).hasSize(1);
        verify(repository).findAll();
    }
}