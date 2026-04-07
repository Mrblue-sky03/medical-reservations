package edu.unimag.domine.api.dto;

import java.io.Serializable;
import java.util.UUID;

public class OfficeDtos {

    public record CreateOfficeRequest(
        String name,
        String location
    ) implements Serializable{}

    public record UpdateOfficeRequest(
            String name,
            String location,
            Boolean active
    ) implements Serializable {}

    public record OfficeResponse(
            UUID id,
            String name,
            String location,
            Boolean active
    ) implements Serializable {}
}
