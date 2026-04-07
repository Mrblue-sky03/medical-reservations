package edu.unimag.domine.service;

import edu.unimag.domine.api.dto.DoctorDtos.CreateDoctorRequest;
import edu.unimag.domine.api.dto.DoctorDtos.DoctorResponse;
import edu.unimag.domine.api.dto.DoctorDtos.UpdateDoctorRequest;
import edu.unimag.domine.entities.Doctor;
import edu.unimag.domine.entities.enums.DocumentType;
import edu.unimag.domine.exceptions.ResourceNotFoundException;
import edu.unimag.domine.exceptions.ValidationException;
import edu.unimag.domine.mappers.DoctorMapper;
import edu.unimag.domine.repositories.DoctorRepository;
import edu.unimag.domine.service.implementation.DoctorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceImplTest {

    @Mock private DoctorRepository repository;
    @Mock private DoctorMapper mapper;

    @InjectMocks private DoctorServiceImpl service;

    private final UUID id = UUID.randomUUID();
    private final UUID specialtyId = UUID.randomUUID();

    @Test
    void shouldCreateSuccessfully() {

        var request = new CreateDoctorRequest(specialtyId, "Dr. House", "house@mail.com", "555-1234", "123456789", DocumentType.CC, "LIC-999");
        var entity = new Doctor();
        var response = new DoctorResponse(id, specialtyId, "Dr. House", "house@mail.com", "555-1234", "123456789", DocumentType.CC, "LIC-999", true);

        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        var result = service.create(request);

        assertThat(result).isNotNull();
        assertThat(result.fullName()).isEqualTo("Dr. House");
        verify(repository).save(any());
    }

    @Test
    void shouldGetByIdSuccessfully() {
        var entity = new Doctor();
        var response = new DoctorResponse(id, specialtyId, "Dr. House", "house@mail.com", "555", "123", DocumentType.CC, "LIC", true);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        var result = service.getDoctorById(id);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);
    }

    @Test
    void shouldThrowWhenDoctorNotFound() {
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getDoctorById(id));
    }

    @Test
    void shouldUpdateSuccessfully() {
        var request = new UpdateDoctorRequest(specialtyId, "Dr. Gregory House", "house@mail.com", "555-0000", "LIC-999", "123456789", true);
        var entity = new Doctor();
        var response = new DoctorResponse(id, specialtyId, "Dr. Gregory House", "house@mail.com", "555-0000", "123456789", DocumentType.CC, "LIC-999", true);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        var result = service.updateDoctor(id, request);

        assertThat(result).isNotNull();
        assertThat(result.fullName()).isEqualTo("Dr. Gregory House");
        verify(mapper).update(request, entity);
    }
}