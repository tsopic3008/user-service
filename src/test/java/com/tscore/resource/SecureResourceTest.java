package com.tscore.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class SecureResourceTest {

    @Test
    @TestSecurity(user = "johndoe", roles = "user")
    void secureEndpoint_authenticatedUser_returns200WithGreeting() {
        given()
                .when().get("/secure")
                .then()
                .statusCode(200)
                .body("message", containsString("johndoe"));
    }

    @Test
    void secureEndpoint_unauthenticated_returns401() {
        given()
                .when().get("/secure")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "admin", roles = "admin")
    void secureEndpoint_wrongRole_returns403() {
        given()
                .when().get("/secure")
                .then()
                .statusCode(403);
    }
}
