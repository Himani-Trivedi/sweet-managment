package com.api.mithai.service;

import com.api.mithai.auth.dto.RegisterAuthRequestDto;
import com.api.mithai.auth.entity.User;
import com.api.mithai.auth.enums.Role;
import com.api.mithai.auth.repository.UserRepository;
import com.api.mithai.auth.service.AuthService;
import com.api.mithai.base.exception.ResponseStatusException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Login Tests")
public class AuthServiceRegisterUserTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;


    @Nested
    @DisplayName("Register User Tests")
    class RegisterUserTests {

        private RegisterAuthRequestDto registerRequest;

        @BeforeEach
        void setUp() {
            registerRequest = new RegisterAuthRequestDto();
            registerRequest.setEmailId("user@example.com");
            registerRequest.setPassword("SecureP@1");
        }

        @Nested
        @DisplayName("Register Email Validation Tests")
        class RegisterEmailValidationTests {

            @Test
            @DisplayName("Should throw exception when emailId is null")
            void shouldThrowExceptionWhenEmailIdIsNull() {
                // Given
                registerRequest.setEmailId(null);

                // When & Then
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    authService.register(registerRequest);
                });
                assertEquals("Email ID cannot be null", exception.getMessage());
                verify(userRepository, never()).existsByEmailId(anyString());
                verify(userRepository, never()).save(any(User.class));
            }

            @Test
            @DisplayName("Should throw exception when emailId is empty")
            void shouldThrowExceptionWhenEmailIdIsEmpty() {
                // Given
                registerRequest.setEmailId("");

                // When & Then
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    authService.register(registerRequest);
                });
                assertEquals("Email ID cannot be empty", exception.getMessage());
                verify(userRepository, never()).existsByEmailId(anyString());
                verify(userRepository, never()).save(any(User.class));
            }

            @Test
            @DisplayName("Should throw exception when emailId is blank")
            void shouldThrowExceptionWhenEmailIdIsBlank() {
                // Given
                registerRequest.setEmailId("   ");

                // When & Then
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    authService.register(registerRequest);
                });
                assertEquals("Email ID cannot be blank", exception.getMessage());
                verify(userRepository, never()).existsByEmailId(anyString());
                verify(userRepository, never()).save(any(User.class));
            }

            @Test
            @DisplayName("Should throw exception when emailId format is invalid")
            void shouldThrowExceptionWhenEmailIdFormatIsInvalid() {
                // Given
                registerRequest.setEmailId("invalid-email");

                // When & Then
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    authService.register(registerRequest);
                });
                assertEquals("Email ID format is invalid", exception.getMessage());
                verify(userRepository, never()).existsByEmailId(anyString());
                verify(userRepository, never()).save(any(User.class));
            }
        }

        @Nested
        @DisplayName("Register Password Validation Tests")
        class RegisterPasswordValidationTests {

            @Test
            @DisplayName("Should throw exception when password is null")
            void shouldThrowExceptionWhenPasswordIsNull() {
                // Given
                registerRequest.setPassword(null);

                // When & Then
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    authService.register(registerRequest);
                });
                assertEquals("Password cannot be null", exception.getMessage());
                verify(userRepository, never()).existsByEmailId(anyString());
                verify(userRepository, never()).save(any(User.class));
            }

            @Test
            @DisplayName("Should throw exception when password is empty")
            void shouldThrowExceptionWhenPasswordIsEmpty() {
                // Given
                registerRequest.setPassword("");

                // When & Then
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    authService.register(registerRequest);
                });
                assertEquals("Password cannot be empty", exception.getMessage());
                verify(userRepository, never()).existsByEmailId(anyString());
                verify(userRepository, never()).save(any(User.class));
            }

            @Test
            @DisplayName("Should throw exception when password is too short")
            void shouldThrowExceptionWhenPasswordIsTooShort() {
                // Given
                registerRequest.setPassword("Short1@");

                // When & Then
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    authService.register(registerRequest);
                });
                assertEquals("Password must be between 8 and 12 characters", exception.getMessage());
                verify(userRepository, never()).existsByEmailId(anyString());
                verify(userRepository, never()).save(any(User.class));
            }

            @Test
            @DisplayName("Should throw exception when password is too long")
            void shouldThrowExceptionWhenPasswordIsTooLong() {
                // Given
                registerRequest.setPassword("VeryLongPassword123@");

                // When & Then
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    authService.register(registerRequest);
                });
                assertEquals("Password must be between 8 and 12 characters", exception.getMessage());
                verify(userRepository, never()).existsByEmailId(anyString());
                verify(userRepository, never()).save(any(User.class));
            }

            @Test
            @DisplayName("Should throw exception when password lacks uppercase letter")
            void shouldThrowExceptionWhenPasswordLacksUppercase() {
                // Given
                registerRequest.setPassword("securep@1");

                // When & Then
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    authService.register(registerRequest);
                });
                assertEquals("Password must contain at least one uppercase letter", exception.getMessage());
                verify(userRepository, never()).existsByEmailId(anyString());
                verify(userRepository, never()).save(any(User.class));
            }

            @Test
            @DisplayName("Should throw exception when password lacks lowercase letter")
            void shouldThrowExceptionWhenPasswordLacksLowercase() {
                // Given
                registerRequest.setPassword("SECUREP@1");

                // When & Then
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    authService.register(registerRequest);
                });
                assertEquals("Password must contain at least one lowercase letter", exception.getMessage());
                verify(userRepository, never()).existsByEmailId(anyString());
                verify(userRepository, never()).save(any(User.class));
            }

            @Test
            @DisplayName("Should throw exception when password lacks special character")
            void shouldThrowExceptionWhenPasswordLacksSpecialCharacter() {
                // Given
                registerRequest.setPassword("SecureP12");

                // When & Then
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    authService.register(registerRequest);
                });
                assertEquals("Password must contain at least one special character", exception.getMessage());
                verify(userRepository, never()).existsByEmailId(anyString());
                verify(userRepository, never()).save(any(User.class));
            }
        }

        @Nested
        @DisplayName("Register User Repository Tests")
        class RegisterUserRepositoryTests {

            @Test
            @DisplayName("Should throw ResponseStatusException when user already exists")
            void shouldThrowResponseStatusExceptionWhenUserAlreadyExists() {
                // Given
                registerRequest.setEmailId("existing@example.com");

                when(userRepository.existsByEmailId("existing@example.com")).thenReturn(true);

                // When & Then
                ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                    authService.register(registerRequest);
                });
                assertEquals("User with this email already exists", exception.getMessage());
                verify(userRepository, times(1)).existsByEmailId("existing@example.com");
                verify(userRepository, never()).save(any(User.class));
            }

            @Test
            @DisplayName("Should proceed when user does not exist")
            void shouldProceedWhenUserDoesNotExist() {
                // Given
                registerRequest.setEmailId("newuser@example.com");

                when(userRepository.existsByEmailId("newuser@example.com")).thenReturn(false);
                when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

                // When & Then
                assertDoesNotThrow(() -> {
                    authService.register(registerRequest);
                });
                verify(userRepository, times(1)).existsByEmailId("newuser@example.com");
            }
        }

        @Nested
        @DisplayName("Register Role Type Tests")
        class RegisterRoleTypeTests {

            @Test
            @DisplayName("Should create user with USER role by default")
            void shouldCreateUserWithUserRoleByDefault() {
                // Given
                registerRequest.setEmailId("newuser@example.com");

                when(userRepository.existsByEmailId("newuser@example.com")).thenReturn(false);
                when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

                // When
                authService.register(registerRequest);

                // Then
                verify(userRepository, times(1)).save(argThat(user ->
                        user.getRoleName() == Role.USER
                ));
            }
        }

        @Nested
        @DisplayName("Register Success Tests")
        class RegisterSuccessTests {

            @Test
            @DisplayName("Should register user successfully with valid data")
            void shouldRegisterUserSuccessfullyWithValidData() {
                // Given
                registerRequest.setEmailId("newuser@example.com");

                when(userRepository.existsByEmailId("newuser@example.com")).thenReturn(false);
                when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

                // When
                authService.register(registerRequest);

                // Then
                verify(userRepository, times(1)).existsByEmailId("newuser@example.com");
                verify(userRepository, times(1)).save(any(User.class));
            }

            @Test
            @DisplayName("Should hash password before saving")
            void shouldHashPasswordBeforeSaving() {
                // Given
                registerRequest.setEmailId("newuser@example.com");

                when(userRepository.existsByEmailId("newuser@example.com")).thenReturn(false);
                when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

                // When
                authService.register(registerRequest);

                // Then
                verify(userRepository, times(1)).save(argThat(user ->
                        user.getPassword() != null &&
                                !user.getPassword().equals("SecureP@1") &&
                                user.getPassword().length() > "SecureP@1".length()
                ));
            }

            @Test
            @DisplayName("Should generate username from email")
            void shouldGenerateUsernameFromEmail() {
                // Given
                registerRequest.setEmailId("newuser@example.com");

                when(userRepository.existsByEmailId("newuser@example.com")).thenReturn(false);
                when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

                // When
                authService.register(registerRequest);

                // Then
                verify(userRepository, times(1)).save(argThat(user ->
                        user.getUsername() != null &&
                                !user.getUsername().isEmpty()
                ));
            }
        }

        @Nested
        @DisplayName("Register Failure Tests")
        class RegisterFailureTests {

            @Test
            @DisplayName("Should fail when email validation fails")
            void shouldFailWhenEmailValidationFails() {
                // Given
                registerRequest.setEmailId("invalid-email");

                // When & Then
                assertThrows(IllegalArgumentException.class, () -> {
                    authService.register(registerRequest);
                });
                verify(userRepository, never()).existsByEmailId(anyString());
                verify(userRepository, never()).save(any(User.class));
            }

            @Test
            @DisplayName("Should fail when password validation fails")
            void shouldFailWhenPasswordValidationFails() {
                // Given
                registerRequest.setPassword("short");

                // When & Then
                assertThrows(IllegalArgumentException.class, () -> {
                    authService.register(registerRequest);
                });
                verify(userRepository, never()).existsByEmailId(anyString());
                verify(userRepository, never()).save(any(User.class));
            }

            @Test
            @DisplayName("Should fail when user already exists")
            void shouldFailWhenUserAlreadyExists() {
                // Given
                registerRequest.setEmailId("existing@example.com");

                User existingUser = new User(1L, "existinguser", "existing@example.com", "SecureP@1", Role.USER);
                when(userRepository.existsByEmailId("existing@example.com")).thenReturn(true);

                // When & Then
                ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                    authService.register(registerRequest);
                });
                assertEquals("User with this email already exists", exception.getMessage());
                verify(userRepository, times(1)).existsByEmailId("existing@example.com");
                verify(userRepository, never()).save(any(User.class));
            }
        }
    }
}
