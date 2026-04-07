package edu.unimag.domine.mappers;

import edu.unimag.domine.api.dto.SpecialtyDtos;
import edu.unimag.domine.entities.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

        SpecialtyDtos.SpecialtyResponse toResponse(Specialty specialty);

        @Mapping(target = "id", ignore = true)
        Specialty toEntity(SpecialtyDtos.CreateSpecialtyRequest request);
    }

