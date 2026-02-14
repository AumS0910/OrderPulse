package org.orderpulse.orderpulsebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orderpulse.orderpulsebackend.entity.Role;

/**
 * Authentication response payload.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String username;
    private Role role;
    private long expiresIn;
}
