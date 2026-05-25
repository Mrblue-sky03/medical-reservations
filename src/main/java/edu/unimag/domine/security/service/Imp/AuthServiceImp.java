package edu.unimag.domine.security.service.Imp;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.unimag.domine.security.dto.UserDto.AuthResponse;
import edu.unimag.domine.security.dto.UserDto.LoginRequest;
import edu.unimag.domine.security.dto.UserDto.RegisterRequest;
import edu.unimag.domine.security.entity.Role;
import edu.unimag.domine.security.entity.User;
import edu.unimag.domine.security.jwt.JwtService;
import edu.unimag.domine.security.repository.UserRepository;
import edu.unimag.domine.security.service.AuthService;
import edu.unimag.domine.security.service.UserDetailsServiceImp;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class AuthServiceImp implements AuthService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;  
    private final UserDetailsServiceImp userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Override
    public AuthResponse login(LoginRequest request) {
        var userDetails = userDetailsService.loadUserByUsername(request.username());
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        var claims = Map.<String, Object>of(
            "roles", userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toList()
        );
        
        var token = jwtService.generateToken(userDetails, claims);
        return new AuthResponse(token, "Bearer", jwtService.getExpirationSeconds());
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
            .username(request.username())
            .password(passwordEncoder.encode(request.password())) // ← codifica aquí
            .fullName(request.fullName())
            .email(request.email())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .roles(Set.of(Role.ROLE_USER))
            .build();

        userRepository.save(user);

        var userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        var claims = Map.<String, Object>of(
            "roles", userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toList()
        );

        var token = jwtService.generateToken(userDetails, claims);

        return new AuthResponse(token, "Bearer", jwtService.getExpirationSeconds());
    }
}