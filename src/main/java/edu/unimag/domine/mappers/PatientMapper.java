package edu.unimag.domine.mappers;

import edu.unimag.domine.api.dto.PatientDtos.UpdatePatientRequest;
import edu.unimag.domine.api.dto.PatientDtos.PatientResponse;
import edu.unimag.domine.api.dto.PatientDtos.CreatePatientRequest;
import edu.unimag.domine.entities.Patient;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientResponse toResponse(Patient patient);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Patient toEntity(CreatePatientRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "documentNumber", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void update(UpdatePatientRequest dto, @MappingTarget Patient entity);
}
