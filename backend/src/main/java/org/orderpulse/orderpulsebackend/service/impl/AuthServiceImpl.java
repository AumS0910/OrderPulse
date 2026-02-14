package org.orderpulse.orderpulsebackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.orderpulse.orderpulsebackend.dto.AuthResponse;
import org.orderpulse.orderpulsebackend.dto.LoginRequest;
import org.orderpulse.orderpulsebackend.dto.RegisterRequest;
import org.orderpulse.orderpulsebackend.entity.Role;
import org.orderpulse.orderpulsebackend.entity.User;
import org.orderpulse.orderpulsebackend.repository.UserRepository;
import org.orderpulse.orderpulsebackend.security.JwtUtils;
import org.orderpulse.orderpulsebackend.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication service implementation for register/login/current-user operations.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Value("${app.auth.allow-admin-registration:false}")
    private boolean allowAdminRegistration;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        Role role = resolveRegistrationRole(request);
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtUtils.generateToken(savedUser);

        return AuthResponse.builder()
                .token(token)
                .username(savedUser.getUsername())
                .role(savedUser.getRole())
                .expiresIn(jwtUtils.getExpirationInSeconds())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtUtils.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole())
                .expiresIn(jwtUtils.getExpirationInSeconds())
                .build();
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
            throw new UsernameNotFoundException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return user;
        }

        if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            return userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found"));
        }

        throw new UsernameNotFoundException("No authenticated user found");
    }

    private Role resolveRegistrationRole(RegisterRequest request) {
        Role requestedRole = request.getRole() == null ? Role.ROLE_USER : request.getRole();
        if (requestedRole == Role.ROLE_ADMIN && !allowAdminRegistration) {
            throw new IllegalArgumentException("Admin self-registration is disabled");
        }
        return requestedRole;
    }
}
