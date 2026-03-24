package com.tscore.service;

import com.tscore.dto.RegisterRequest;
import com.tscore.dto.UserDTO;
import com.tscore.exception.RegistrationException;
import com.tscore.model.User;
import com.tscore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    private RegisterRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new RegisterRequest(
                "johndoe",
                "john@example.com",
                "John",
                "Doe",
                "Password123!",
                "Zagreb",
                "Main St 1"
        );
    }

    @Test
    void registerUser_success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        doAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return null;
        }).when(userRepository).persist(any(User.class));

        UserDTO result = userService.registerUser(validRequest);

        assertThat(result.username()).isEqualTo("johndoe");
        assertThat(result.email()).isEqualTo("john@example.com");
        assertThat(result.firstName()).isEqualTo("John");
        assertThat(result.lastName()).isEqualTo("Doe");
        assertThat(result.city()).isEqualTo("Zagreb");
    }

    @Test
    void registerUser_emailAlreadyInUse_throwsRegistrationException() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(validRequest))
                .isInstanceOf(RegistrationException.class)
                .hasMessageContaining("Email already in use");

        verify(userRepository, never()).persist(any(User.class));
    }

    @Test
    void registerUser_usernameAlreadyTaken_throwsRegistrationException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername("johndoe")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(validRequest))
                .isInstanceOf(RegistrationException.class)
                .hasMessageContaining("Username already taken");

        verify(userRepository, never()).persist(any(User.class));
    }

    @Test
    void registerUser_passwordIsHashed() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        doAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return null;
        }).when(userRepository).persist(any(User.class));

        userService.registerUser(validRequest);

        verify(userRepository).persist(captor.capture());
        String storedPassword = captor.getValue().getPassword();
        assertThat(storedPassword).isNotEqualTo("Password123!");
        assertThat(storedPassword).startsWith("$2a$");
    }

    @Test
    void findById_existingUser_returnsDto() {
        User user = buildUser(1L, "johndoe", "john@example.com");
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(user));

        Optional<UserDTO> result = userService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(1L);
        assertThat(result.get().username()).isEqualTo("johndoe");
    }

    @Test
    void findById_notFound_returnsEmpty() {
        when(userRepository.findByIdOptional(99L)).thenReturn(Optional.empty());

        Optional<UserDTO> result = userService.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void findByUsername_existingUser_returnsDto() {
        User user = buildUser(1L, "johndoe", "john@example.com");
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));

        Optional<UserDTO> result = userService.findByUsername("johndoe");

        assertThat(result).isPresent();
        assertThat(result.get().email()).isEqualTo("john@example.com");
    }

    private User buildUser(Long id, String username, String email) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail(email);
        user.setPassword("$2a$hash");
        return user;
    }
}
