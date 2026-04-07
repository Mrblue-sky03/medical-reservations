package edu.unimag.domine.mappers;

import edu.unimag.domine.api.dto.DoctorScheduleDtos.DoctorScheduleResponse;
import edu.unimag.domine.api.dto.DoctorScheduleDtos.CreateDoctorScheduleRequest;
import edu.unimag.domine.entities.DoctorSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorScheduleMapper {

        @Mapping(source = "doctor.id", target = "doctorId")
        DoctorScheduleResponse toResponse(DoctorSchedule schedule);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "doctor", ignore = true)
        @Mapping(target = "startsAt", ignore = true)
        @Mapping(target = "endsAt", ignore = true)
        DoctorSchedule toEntity(CreateDoctorScheduleRequest request);
    }