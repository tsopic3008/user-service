package com.tscore.dto;

import java.util.List;

public record KeycloakUserRepresentation(
        String username,
        String email,
        String firstName,
        String lastName,
        boolean enabled,
        boolean emailVerified,
        List<CredentialRepresentation> credentials
) {
    public static KeycloakUserRepresentation from(RegisterRequest req) {
        return new KeycloakUserRepresentation(
                req.username(),
                req.email(),
                req.firstName(),
                req.lastName(),
                true,
                true,
                List.of(new CredentialRepresentation("password", req.password(), false))
        );
    }
}
