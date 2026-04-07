package edu.unimag.domine.service;

import edu.unimag.domine.api.dto.PatientDtos.CreatePatientRequest;
import edu.unimag.domine.api.dto.PatientDtos.PatientResponse;
import edu.unimag.domine.api.dto.PatientDtos.UpdatePatientRequest;
import edu.unimag.domine.entities.Patient;
import edu.unimag.domine.exceptions.ResourceNotFoundException;
import edu.unimag.domine.exceptions.ValidationException;
import edu.unimag.domine.mappers.PatientMapper;
import edu.unimag.domine.repositories.PatientRepository;
import edu.unimag.domine.service.implementation.PatientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock private PatientRepository repository;
    @Mock private PatientMapper mapper;

    @InjectMocks private PatientServiceImpl service;

    private final UUID id = UUID.randomUUID();

    @Test
    void shouldCreateSuccessfully() {
        var request = new CreatePatientRequest("John Doe", null, "12345", "john@mail.com", "555-9999", LocalDate.of(1990, 5, 15));
        var entity = new Patient();
        var response = new PatientResponse(id, "John Doe", null, "12345", "john@mail.com", "555-9999", true, LocalDate.of(1990, 5, 15));

        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        var result = service.create(request);

        assertThat(result.fullName()).isEqualTo("John Doe");
        verify(repository).save(any());
    }

    @Test
    void shouldGetByIdSuccessfully() {
        var entity = new Patient();
        var response = new PatientResponse(id, "John Doe", null, "12345", "john@mail.com", "555", true, LocalDate.now());

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        var result = service.getById(id);
        assertThat(result).isNotNull();
    }

    @Test
    void shouldUpdateSuccessfully() {
        var request = new UpdatePatientRequest("John Doe Updated", "john.new@mail.com", "555-0000", null);
        var entity = new Patient();
        var response = new PatientResponse(id, "John Doe Updated", null, "12345", "john.new@mail.com", "555-0000", true, LocalDate.now());

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        var result = service.update(id, request);

        assertThat(result.fullName()).isEqualTo("John Doe Updated");
        verify(mapper).update(request, entity);
    }
}