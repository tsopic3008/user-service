package com.tscore.service;

import com.tscore.client.KeycloakAdminClient;
import com.tscore.client.KeycloakTokenClient;
import com.tscore.dto.KeycloakUserRepresentation;
import com.tscore.dto.RegisterRequest;
import com.tscore.exception.RegistrationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeycloakServiceTest {

    @Mock
    KeycloakTokenClient tokenClient;

    @Mock
    KeycloakAdminClient adminClient;

    @InjectMocks
    KeycloakService keycloakService;

    private RegisterRequest validRequest;

    @BeforeEach
    void setUp() {
        // Inject @ConfigProperty values manually (no CDI container in pure unit test)
        keycloakService = new KeycloakService();
        injectField(keycloakService, "clientId", "tscore");
        injectField(keycloakService, "clientSecret", "secret");
        injectField(keycloakService, "realm", "tscore");
        injectField(keycloakService, "tokenClient", tokenClient);
        injectField(keycloakService, "adminClient", adminClient);

        validRequest = new RegisterRequest("johndoe", "john@example.com", "John", "Doe", "Password1!", null, null);
    }

    @Test
    void getServiceAccountToken_returnsAccessToken() {
        when(tokenClient.getToken("client_credentials", "tscore", "secret"))
                .thenReturn(Map.of("access_token", "service-token-xyz"));

        String token = keycloakService.getServiceAccountToken();

        assertThat(token).isEqualTo("service-token-xyz");
    }

    @Test
    void registerUserInKeycloak_created_succeeds() {
        when(tokenClient.getToken(anyString(), anyString(), anyString()))
                .thenReturn(Map.of("access_token", "admin-token"));
        Response created = mock(Response.class);
        when(created.getStatus()).thenReturn(201);
        when(adminClient.createUser(eq("tscore"), anyString(), any(KeycloakUserRepresentation.class)))
                .thenReturn(created);

        assertThatNoException().isThrownBy(() -> keycloakService.registerUserInKeycloak(validRequest));
    }

    @Test
    void registerUserInKeycloak_conflict_throwsRegistrationException() {
        when(tokenClient.getToken(anyString(), anyString(), anyString()))
                .thenReturn(Map.of("access_token", "admin-token"));
        Response conflict = mock(Response.class);
        when(conflict.getStatus()).thenReturn(409);
        when(adminClient.createUser(anyString(), anyString(), any()))
                .thenReturn(conflict);

        assertThatThrownBy(() -> keycloakService.registerUserInKeycloak(validRequest))
                .isInstanceOf(RegistrationException.class)
                .hasMessageContaining("already exists in Keycloak");
    }

    @Test
    void registerUserInKeycloak_serverError_throwsRegistrationException() {
        when(tokenClient.getToken(anyString(), anyString(), anyString()))
                .thenReturn(Map.of("access_token", "admin-token"));
        Response serverError = mock(Response.class);
        when(serverError.getStatus()).thenReturn(500);
        when(serverError.readEntity(String.class)).thenReturn("Internal Server Error");
        when(adminClient.createUser(anyString(), anyString(), any()))
                .thenReturn(serverError);

        assertThatThrownBy(() -> keycloakService.registerUserInKeycloak(validRequest))
                .isInstanceOf(RegistrationException.class)
                .hasMessageContaining("failed with status 500");
    }

    // Reflection helper to inject fields without CDI in pure unit tests
    private void injectField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject field: " + fieldName, e);
        }
    }
}
