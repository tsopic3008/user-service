package com.tscore.service;

import com.tscore.dto.RegisterRequest;
import com.tscore.dto.UserDTO;
import com.tscore.exception.RegistrationException;
import com.tscore.model.User;
import com.tscore.repository.UserRepository;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @Transactional
    public UserDTO registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RegistrationException("Email already in use: " + request.email());
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new RegistrationException("Username already taken: " + request.username());
        }

        User user = toEntity(request);
        user.setPassword(BCrypt.hashpw(request.password(), BCrypt.gensalt()));
        userRepository.persist(user);

        Log.infof("User persisted with id=%d, username=%s", user.getId(), user.getUsername());
        return UserDTO.from(user);
    }

    public Optional<UserDTO> findById(Long id) {
        return userRepository.findByIdOptional(id).map(UserDTO::from);
    }

    public Optional<UserDTO> findByUsername(String username) {
        return userRepository.findByUsername(username).map(UserDTO::from);
    }

    private User toEntity(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setCity(request.city());
        user.setAddress(request.address());
        return user;
    }
}
