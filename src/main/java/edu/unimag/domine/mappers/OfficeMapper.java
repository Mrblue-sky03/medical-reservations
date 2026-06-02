package edu.unimag.domine.mappers;

import edu.unimag.domine.api.dto.OfficeDtos.OfficeResponse;
import edu.unimag.domine.api.dto.OfficeDtos.CreateOfficeRequest;
import edu.unimag.domine.api.dto.OfficeDtos.UpdateOfficeRequest;
import edu.unimag.domine.entities.Office;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OfficeMapper {

        OfficeResponse toResponse(Office office);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "appointments", ignore = true)
        @Mapping(target = "active", ignore = true)
        @Mapping(target = "description", ignore = true)
        @Mapping(target = "createdAt", ignore = true)
        @Mapping(target = "updatedAt", ignore = true)
        Office toEntity(CreateOfficeRequest request);

        @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
        @Mapping(target = "id", ignore = true)
        @Mapping(target = "appointments", ignore = true)
        @Mapping(target = "description", ignore = true)
        @Mapping(target = "createdAt", ignore = true)
        @Mapping(target = "updatedAt", ignore = true)
        void update(UpdateOfficeRequest dto, @MappingTarget Office entity);
    }

