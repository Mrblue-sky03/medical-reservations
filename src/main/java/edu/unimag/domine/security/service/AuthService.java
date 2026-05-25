package edu.unimag.domine.security.service;

import edu.unimag.domine.security.dto.UserDto.AuthResponse;
import edu.unimag.domine.security.dto.UserDto.LoginRequest;
import edu.unimag.domine.security.dto.UserDto.RegisterRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
}