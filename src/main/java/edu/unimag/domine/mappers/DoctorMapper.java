package edu.unimag.domine.mappers;

import edu.unimag.domine.api.dto.DoctorDtos.DoctorResponse;
import edu.unimag.domine.api.dto.DoctorDtos.CreateDoctorRequest;
import edu.unimag.domine.api.dto.DoctorDtos.UpdateDoctorRequest;
import edu.unimag.domine.entities.Doctor;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DoctorMapper {


        @Mapping(source = "specialty.id", target = "specialtyId")
        @Mapping(source = "licenseNumber", target = "numberLicense")
        DoctorResponse toResponse(Doctor doctor);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "specialty", ignore = true)
        @Mapping(target = "active", ignore = true)
        @Mapping(target = "schedules", ignore = true)
        @Mapping(target = "appointments", ignore = true)
        @Mapping(target = "createdAt", ignore = true)
        @Mapping(target = "updatedAt", ignore = true)
        @Mapping(source = "numberLicense", target = "licenseNumber")
        Doctor toEntity(CreateDoctorRequest request);

        @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
        @Mapping(target = "id", ignore = true)
        @Mapping(target = "specialty", ignore = true)
        @Mapping(target = "createdAt", ignore = true)
        @Mapping(target = "updatedAt", ignore = true)
        @Mapping(source = "numberLicense", target = "licenseNumber")  
        void update(UpdateDoctorRequest dto, @MappingTarget Doctor entity);

    }

