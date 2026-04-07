package edu.unimag.domine.mappers;

import edu.unimag.domine.api.dto.AppointmentDtos.CreateAppointmentRequest;
import edu.unimag.domine.api.dto.AppointmentDtos.AppointmentResponse;
import edu.unimag.domine.entities.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(source = "office.id", target = "officeId")
    @Mapping(source = "appointmentType.id", target = "appointmentTypeId")
    AppointmentResponse toResponse(Appointment appointment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "appointmentType", ignore = true)
    @Mapping(target = "office", ignore = true)
    @Mapping(target = "endAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Appointment toEntity(CreateAppointmentRequest request);
}
