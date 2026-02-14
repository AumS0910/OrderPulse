package org.orderpulse.orderpulsebackend.service;

import org.orderpulse.orderpulsebackend.dto.AuthResponse;
import org.orderpulse.orderpulsebackend.dto.LoginRequest;
import org.orderpulse.orderpulsebackend.dto.RegisterRequest;
import org.orderpulse.orderpulsebackend.entity.User;

/**
 * Service contract for authentication and user identity operations.
 */
public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    User getCurrentUser();
}
