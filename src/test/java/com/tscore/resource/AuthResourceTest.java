package com.tscore.resource;

import com.tscore.client.MailingClient;
import com.tscore.dto.LoginRequest;
import com.tscore.dto.RegisterRequest;
import com.tscore.dto.UserDTO;
import com.tscore.service.AuthService;
import com.tscore.service.KeycloakService;
import com.tscore.service.UserService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
class AuthResourceTest {

    @InjectMock
    AuthService authService;

    @InjectMock
    KeycloakService keycloakService;

    @InjectMock
    UserService userService;

    @InjectMock
    @RestClient
    MailingClient mailingClient;

    @Test
    void login_validCredentials_returns200WithToken() {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(Map.of("access_token", "mock-jwt-token", "token_type", "Bearer"));

        given()
                .contentType(ContentType.JSON)
                .body(Map.of("username", "johndoe", "password", "secret"))
                .when().post("/auth/login")
                .then()
                .statusCode(200)
                .body("access_token", equalTo("mock-jwt-token"));
    }

    @Test
    void login_invalidCredentials_returns401() {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new WebApplicationException(Response.status(401).build()));

        given()
                .contentType(ContentType.JSON)
                .body(Map.of("username", "bad", "password", "wrong"))
                .when().post("/auth/login")
                .then()
                .statusCode(401);
    }

    @Test
    void login_missingUsername_returns400() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("password", "secret"))
                .when().post("/auth/login")
                .then()
                .statusCode(400);
    }

    @Test
    void register_validRequest_returns201WithUserDTO() {
        UserDTO createdUser = new UserDTO(1L, "johndoe", "John", "Doe", "Zagreb", null, "john@example.com");
        doNothing().when(keycloakService).registerUserInKeycloak(any(RegisterRequest.class));
        when(userService.registerUser(any(RegisterRequest.class))).thenReturn(createdUser);

        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "username", "johndoe",
                        "email", "john@example.com",
                        "firstName", "John",
                        "lastName", "Doe",
                        "password", "Password123!",
                        "city", "Zagreb"
                ))
                .when().post("/auth/register")
                .then()
                .statusCode(201)
                .body("username", equalTo("johndoe"))
                .body("email", equalTo("john@example.com"));
    }

    @Test
    void register_duplicateUser_returns409() {
        doThrow(new com.tscore.exception.RegistrationException("User already exists in Keycloak: johndoe"))
                .when(keycloakService).registerUserInKeycloak(any(RegisterRequest.class));

        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "username", "johndoe",
                        "email", "john@example.com",
                        "firstName", "John",
                        "lastName", "Doe",
                        "password", "Password123!"
                ))
                .when().post("/auth/register")
                .then()
                .statusCode(409)
                .body("error", containsString("already exists"));
    }

    @Test
    void register_invalidEmail_returns400() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "username", "johndoe",
                        "email", "not-an-email",
                        "firstName", "John",
                        "lastName", "Doe",
                        "password", "Password123!"
                ))
                .when().post("/auth/register")
                .then()
                .statusCode(400);
    }

    @Test
    void register_shortPassword_returns400() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "username", "johndoe",
                        "email", "john@example.com",
                        "firstName", "John",
                        "lastName", "Doe",
                        "password", "short"
                ))
                .when().post("/auth/register")
                .then()
                .statusCode(400);
    }
}
