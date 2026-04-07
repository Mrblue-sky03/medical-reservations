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
        Office toEntity(CreateOfficeRequest request);

        @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
        @Mapping(target = "id", ignore = true)
        void update(UpdateOfficeRequest dto, @MappingTarget Office entity);
    }

