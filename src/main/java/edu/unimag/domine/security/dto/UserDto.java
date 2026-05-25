package edu.unimag.domine.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserDto {

    public record RegisterRequest(

            @Email @NotBlank String email,
            @NotBlank String password,
            @NotBlank String username,
            @NotBlank String fullName

    ) {}

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {}

    public record AuthResponse(
            String accessToken,
            String tokenType,
            long expiresInSeconds
    ) {}
    
}
