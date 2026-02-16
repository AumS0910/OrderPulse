package org.orderpulse.orderpulsebackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.orderpulse.orderpulsebackend.dto.AuthResponse;
import org.orderpulse.orderpulsebackend.dto.LoginRequest;
import org.orderpulse.orderpulsebackend.dto.RegisterRequest;
import org.orderpulse.orderpulsebackend.entity.Role;
import org.orderpulse.orderpulsebackend.entity.User;
import org.orderpulse.orderpulsebackend.repository.UserRepository;
import org.orderpulse.orderpulsebackend.security.JwtUtils;
import org.orderpulse.orderpulsebackend.service.impl.AuthServiceImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .firstName("Aum")
                .lastName("Shah")
                .username("aum")
                .password("password123")
                .role(Role.ROLE_USER)
                .build();

        loginRequest = LoginRequest.builder()
                .username("aum")
                .password("password123")
                .build();

        user = User.builder()
                .id(1L)
                .firstName("Aum")
                .lastName("Shah")
                .username("aum")
                .password("encoded-password")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    @DisplayName("Should register user successfully")
    void register_Success() {
        when(userRepository.existsByUsername("aum")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtils.generateToken(any(User.class))).thenReturn("jwt-token");
        when(jwtUtils.getExpirationInSeconds()).thenReturn(86400L);

        AuthResponse response = authService.register(registerRequest);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUsername()).isEqualTo("aum");
        assertThat(response.getRole()).isEqualTo(Role.ROLE_USER);
    }

    @Test
    @DisplayName("Should fail register when username already exists")
    void register_DuplicateUsername() {
        when(userRepository.existsByUsername("aum")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username is already taken");
    }

    @Test
    @DisplayName("Should login successfully")
    void login_Success() {
        when(userRepository.findByUsername("aum")).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken(any(User.class))).thenReturn("jwt-token");
        when(jwtUtils.getExpirationInSeconds()).thenReturn(86400L);

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUsername()).isEqualTo("aum");
        assertThat(response.getRole()).isEqualTo(Role.ROLE_USER);
    }
}
