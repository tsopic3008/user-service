package com.tscore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username is required") String username,
        @NotBlank @Email(message = "Valid email is required") String email,
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Last name is required") String lastName,
        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password,
        String city,
        String address
) {}
