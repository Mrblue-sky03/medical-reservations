package edu.unimag.domine.mappers;


import edu.unimag.domine.api.dto.AppointmentTypesDtos.AppointmentTypeResponse;
import edu.unimag.domine.api.dto.AppointmentTypesDtos.CreateAppointmentTypeRequest;
import edu.unimag.domine.entities.AppointmentType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentTypeMapper {

        AppointmentTypeResponse toResponse(AppointmentType type);

        @Mapping(target = "id", ignore = true)
        AppointmentType toEntity(CreateAppointmentTypeRequest request);
    }

