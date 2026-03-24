package com.tscore.resource;

import com.tscore.dto.UserDTO;
import com.tscore.service.UserService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@QuarkusTest
class UserResourceTest {

    @InjectMock
    UserService userService;

    @Test
    @TestSecurity(user = "johndoe", roles = "user")
    void getById_existingUser_returns200() {
        UserDTO dto = new UserDTO(1L, "johndoe", "John", "Doe", "Zagreb", null, "john@example.com");
        when(userService.findById(1L)).thenReturn(Optional.of(dto));

        given()
                .accept(ContentType.JSON)
                .when().get("/users/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("username", equalTo("johndoe"))
                .body("email", equalTo("john@example.com"));
    }

    @Test
    @TestSecurity(user = "johndoe", roles = "user")
    void getById_notFound_returns404() {
        when(userService.findById(99L)).thenReturn(Optional.empty());

        given()
                .accept(ContentType.JSON)
                .when().get("/users/99")
                .then()
                .statusCode(404);
    }

    @Test
    void getById_unauthenticated_returns401() {
        given()
                .accept(ContentType.JSON)
                .when().get("/users/1")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "johndoe", roles = "user")
    void getByUsername_existingUser_returns200() {
        UserDTO dto = new UserDTO(1L, "johndoe", "John", "Doe", null, null, "john@example.com");
        when(userService.findByUsername("johndoe")).thenReturn(Optional.of(dto));

        given()
                .accept(ContentType.JSON)
                .when().get("/users/me/johndoe")
                .then()
                .statusCode(200)
                .body("username", equalTo("johndoe"));
    }

    @Test
    @TestSecurity(user = "johndoe", roles = "user")
    void getByUsername_notFound_returns404() {
        when(userService.findByUsername("unknown")).thenReturn(Optional.empty());

        given()
                .accept(ContentType.JSON)
                .when().get("/users/me/unknown")
                .then()
                .statusCode(404);
    }
}
