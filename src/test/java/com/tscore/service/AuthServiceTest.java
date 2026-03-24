package com.tscore.service;

import com.tscore.client.KeycloakAuthClient;
import com.tscore.dto.LoginRequest;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    KeycloakAuthClient keycloakAuthClient;

    AuthService authService;

    @BeforeEach
    void setUp() throws Exception {
        authService = new AuthService();
        setField(authService, "clientId", "tscore");
        setField(authService, "clientSecret", "test-secret");
        setField(authService, "keycloakAuthClient", keycloakAuthClient);
    }

    @Test
    void login_validCredentials_returnsTokenMap() {
        Map<String, Object> tokenResponse = Map.of(
                "access_token", "eyJhbGci...",
                "token_type", "Bearer",
                "expires_in", 300
        );
        when(keycloakAuthClient.getToken(eq("password"), eq("tscore"), eq("test-secret"), eq("johndoe"), eq("secret")))
                .thenReturn(tokenResponse);

        Map<String, Object> result = authService.login(new LoginRequest("johndoe", "secret"));

        assertThat(result).containsKey("access_token");
        assertThat(result.get("access_token")).isEqualTo("eyJhbGci...");
    }

    @Test
    void login_invalidCredentials_rethrowsWebApplicationException() {
        Response mockResponse = mock(Response.class);
        when(mockResponse.getStatus()).thenReturn(401);
        WebApplicationException unauthorized = new WebApplicationException("Unauthorized", mockResponse);
        when(keycloakAuthClient.getToken(any(), any(), any(), any(), any()))
                .thenThrow(unauthorized);

        assertThatThrownBy(() -> authService.login(new LoginRequest("bad", "wrong")))
                .isInstanceOf(WebApplicationException.class)
                .extracting(e -> ((WebApplicationException) e).getResponse().getStatus())
                .isEqualTo(401);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
