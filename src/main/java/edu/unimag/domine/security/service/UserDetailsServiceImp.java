package edu.unimag.domine.security.service;

import org.springframework.stereotype.Service;

import edu.unimag.domine.security.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;


@Service
@RequiredArgsConstructor


public class UserDetailsServiceImp implements UserDetailsService {

    private final UserRepository userDetailsRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        var user = userDetailsRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

       var authorities = user.getRoles().stream()
                .map(a -> a.name())
                .map(a -> new SimpleGrantedAuthority(a))
                .collect(Collectors.toSet());
        
        return User.withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(authorities)
            .build();
    }

}