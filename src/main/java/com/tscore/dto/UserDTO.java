package com.tscore.dto;

import com.tscore.model.User;

public record UserDTO(
        Long id,
        String username,
        String firstName,
        String lastName,
        String city,
        String address,
        String email
) {
    public static UserDTO from(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getCity(),
                user.getAddress(),
                user.getEmail()
        );
    }
}
