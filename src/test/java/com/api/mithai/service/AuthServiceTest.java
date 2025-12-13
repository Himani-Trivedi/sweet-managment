package com.api.mithai.service;

import com.api.mithai.auth.dto.LoginRequestDto;
import com.api.mithai.auth.dto.LoginResponseDto;
import com.api.mithai.auth.entity.User;
import com.api.mithai.auth.enums.Role;
import com.api.mithai.auth.repository.UserRepository;
import com.api.mithai.auth.service.AuthService;
import com.api.mithai.base.exception.ResponseStatusException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test cases for AuthService login method following TDD approach
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Login Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Nested
    @DisplayName("Email Validation Tests")
    class EmailValidationTests {

        @Test
        @DisplayName("Should throw exception when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            // Given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail(null);
            loginRequest.setPassword("SecureP@1");

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                authService.login(loginRequest);
            });
            assertEquals("Email ID cannot be null", exception.getMessage());
            verify(userRepository, never()).findByEmailId(anyString());
        }

        @Test
        @DisplayName("Should throw exception when email is empty")
        void shouldThrowExceptionWhenEmailIsEmpty() {
            // Given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("");
            loginRequest.setPassword("SecureP@1");

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                authService.login(loginRequest);
            });
            assertEquals("Email ID cannot be empty", exception.getMessage());
            verify(userRepository, never()).findByEmailId(anyString());
        }

        @Test
        @DisplayName("Should throw exception when email is blank")
        void shouldThrowExceptionWhenEmailIsBlank() {
            // Given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("   ");
            loginRequest.setPassword("SecureP@1");

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                authService.login(loginRequest);
            });
            assertEquals("Email ID cannot be blank", exception.getMessage());
            verify(userRepository, never()).findByEmailId(anyString());
        }

        @Test
        @DisplayName("Should throw exception when email format is invalid")
        void shouldThrowExceptionWhenEmailFormatIsInvalid() {
            // Given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("invalid-email");
            loginRequest.setPassword("SecureP@1");

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                authService.login(loginRequest);
            });
            assertEquals("Email ID format is invalid", exception.getMessage());
            verify(userRepository, never()).findByEmailId(anyString());
        }

        @Test
        @DisplayName("Should accept valid email formats")
        void shouldAcceptValidEmailFormats() {
            String[] validEmails = {
                "user@example.com",
                "test.user@example.co.uk",
                "user123@test-domain.com",
                "firstname.lastname@company.com"
            };

            for (String email : validEmails) {
                // Given
                LoginRequestDto loginRequest = new LoginRequestDto();
                loginRequest.setEmail(email);
                loginRequest.setPassword("SecureP@1");

                // When & Then - Should pass email validation but may fail at user lookup
                assertDoesNotThrow(() -> {
                    when(userRepository.findByEmailId(email)).thenReturn(Optional.empty());
                    try {
                        authService.login(loginRequest);
                    } catch (Exception e) {
                        // Expected to fail at user not found, but email validation should pass
                        assertNotEquals("Email ID format is invalid", e.getMessage());
                        assertNotEquals("Email ID cannot be null", e.getMessage());
                        assertNotEquals("Email ID cannot be empty", e.getMessage());
                        assertNotEquals("Email ID cannot be blank", e.getMessage());
                    }
                }, "Email " + email + " should be valid");
            }
        }

        @Test
        @DisplayName("Should accept email with special characters")
        void shouldAcceptEmailWithSpecialCharacters() {
            // Given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("user+tag@example.com");
            loginRequest.setPassword("SecureP@1");

            // When & Then - Should pass email validation
            assertDoesNotThrow(() -> {
                when(userRepository.findByEmailId("user+tag@example.com")).thenReturn(Optional.empty());
                try {
                    authService.login(loginRequest);
                } catch (Exception e) {
                    // Expected to fail at user not found, but email validation should pass
                    assertNotEquals("Email ID format is invalid", e.getMessage());
                }
            }, "Email with special characters should be valid");
        }
    }

    @Nested
    @DisplayName("Password Validation Tests")
    class PasswordValidationTests {

        @Test
        @DisplayName("Should throw exception when password is null")
        void shouldThrowExceptionWhenPasswordIsNull() {
            // Given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("user@example.com");
            loginRequest.setPassword(null);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                authService.login(loginRequest);
            });
            assertEquals("Password cannot be null", exception.getMessage());
            verify(userRepository, never()).findByEmailId(anyString());
        }

        @Test
        @DisplayName("Should throw exception when password is empty")
        void shouldThrowExceptionWhenPasswordIsEmpty() {
            // Given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("user@example.com");
            loginRequest.setPassword("");

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                authService.login(loginRequest);
            });
            assertEquals("Password cannot be empty", exception.getMessage());
            verify(userRepository, never()).findByEmailId(anyString());
        }

        @Test
        @DisplayName("Should throw exception when password is too short")
        void shouldThrowExceptionWhenPasswordIsTooShort() {
            // Given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("user@example.com");
            loginRequest.setPassword("Short1@");

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                authService.login(loginRequest);
            });
            assertEquals("Password must be between 8 and 12 characters", exception.getMessage());
            verify(userRepository, never()).findByEmailId(anyString());
        }

        @Test
        @DisplayName("Should throw exception when password is too long")
        void shouldThrowExceptionWhenPasswordIsTooLong() {
            // Given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("user@example.com");
            loginRequest.setPassword("VeryLongPassword123@");

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                authService.login(loginRequest);
            });
            assertEquals("Password must be between 8 and 12 characters", exception.getMessage());
            verify(userRepository, never()).findByEmailId(anyString());
        }

        @Test
        @DisplayName("Should throw exception when password lacks uppercase letter")
        void shouldThrowExceptionWhenPasswordLacksUppercase() {
            // Given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("user@example.com");
            loginRequest.setPassword("securep@1");

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                authService.login(loginRequest);
            });
            assertEquals("Password must contain at least one uppercase letter", exception.getMessage());
            verify(userRepository, never()).findByEmailId(anyString());
        }

        @Test
        @DisplayName("Should throw exception when password lacks lowercase letter")
        void shouldThrowExceptionWhenPasswordLacksLowercase() {
            // Given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("user@example.com");
            loginRequest.setPassword("SECUREP@1");

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                authService.login(loginRequest);
            });
            assertEquals("Password must contain at least one lowercase letter", exception.getMessage());
            verify(userRepository, never()).findByEmailId(anyString());
        }

        @Test
        @DisplayName("Should throw exception when password lacks special character")
        void shouldThrowExceptionWhenPasswordLacksSpecialCharacter() {
            // Given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("user@example.com");
            loginRequest.setPassword("SecureP12");

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                authService.login(loginRequest);
            });
            assertEquals("Password must contain at least one special character", exception.getMessage());
            verify(userRepository, never()).findByEmailId(anyString());
        }

        @Test
        @DisplayName("Should accept valid passwords")
        void shouldAcceptValidPasswords() {
            String[] validPasswords = {
                "SecureP@1",
                "MyP@ssw0rd",
                "Test1234@",
                "Pass@1234"
            };

            for (String password : validPasswords) {
                // Given
                LoginRequestDto loginRequest = new LoginRequestDto();
                loginRequest.setEmail("user@example.com");
                loginRequest.setPassword(password);

                // When & Then - Should pass password validation but may fail at user lookup
                assertDoesNotThrow(() -> {
                    when(userRepository.findByEmailId("user@example.com")).thenReturn(Optional.empty());
                    try {
                        authService.login(loginRequest);
                    } catch (Exception e) {
                        // Expected to fail at user not found, but password validation should pass
                        assertNotEquals("Password must be between 8 and 12 characters", e.getMessage());
                        assertNotEquals("Password must contain at least one uppercase letter", e.getMessage());
                        assertNotEquals("Password must contain at least one lowercase letter", e.getMessage());
                        assertNotEquals("Password must contain at least one special character", e.getMessage());
                    }
                }, "Password " + password + " should be valid");
            }
        }

        @Test
        @DisplayName("Should accept password with exactly 8 characters")
        void shouldAcceptPasswordWithExactly8Characters() {
            // Given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("user@example.com");
            loginRequest.setPassword("Secure@1");

            // When & Then - Should pass password validation
            assertDoesNotThrow(() -> {
                when(userRepository.findByEmailId("user@example.com")).thenReturn(Optional.empty());
                try {
                    authService.login(loginRequest);
                } catch (Exception e) {
                    // Expected to fail at user not found, but password validation should pass
                    assertNotEquals("Password must be between 8 and 12 characters", e.getMessage());
                }
            }, "Password with exactly 8 characters should be valid");
        }
    }

    @Nested
    @DisplayName("User Repository Tests")
    class UserRepositoryTests {

        @Test
        @DisplayName("Should throw ResponseStatusException when user not found")
        void shouldThrowResponseStatusExceptionWhenUserNotFound() {
            // Given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("nonexistent@example.com");
            loginRequest.setPassword("SecureP@1");

            when(userRepository.findByEmailId("nonexistent@example.com")).thenReturn(Optional.empty());

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                authService.login(loginRequest);
            });
            assertEquals("Invalid email or password", exception.getMessage());
            verify(userRepository, times(1)).findByEmailId("nonexistent@example.com");
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("Should find user with valid email and check password match")
        void shouldFindUserWithValidEmailAndCheckPasswordMatch() {
            // Given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("user@example.com");
            loginRequest.setPassword("SecureP@1");

            User existingUser = new User(1L, "johndoe", "user@example.com", "SecureP@1", Role.USER);
            when(userRepository.findByEmailId("user@example.com")).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches("SecureP@1", existingUser.getPassword())).thenReturn(true);

            // When
            var result = authService.login(loginRequest);

            // Then
            assertNotNull(result);
            verify(userRepository, times(1)).findByEmailId("user@example.com");
            verify(passwordEncoder, times(1)).matches("SecureP@1", existingUser.getPassword());
        }
    }

    @Nested
    @DisplayName("Password Matching Tests")
    class PasswordMatchingTests {

        @Test
        @DisplayName("Should throw ResponseStatusException when password does not match with DB")
        void shouldThrowResponseStatusExceptionWhenPasswordDoesNotMatch() {
            // Given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("user@example.com");
            loginRequest.setPassword("WrongP@1");

            User existingUser = new User(1L, "johndoe", "user@example.com", "SecureP@1", Role.USER);
            when(userRepository.findByEmailId("user@example.com")).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches("WrongP@1", existingUser.getPassword())).thenReturn(false);

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                authService.login(loginRequest);
            });
            assertEquals("Invalid email or password", exception.getMessage());
            verify(userRepository, times(1)).findByEmailId("user@example.com");
            verify(passwordEncoder, times(1)).matches("WrongP@1", existingUser.getPassword());
        }

        @Test
        @DisplayName("Should succeed when password matches")
        void shouldSucceedWhenPasswordMatches() {
            // Given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("user@example.com");
            loginRequest.setPassword("SecureP@1");

            User existingUser = new User(1L, "johndoe", "user@example.com", "SecureP@1", Role.USER);
            when(userRepository.findByEmailId("user@example.com")).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches("SecureP@1", existingUser.getPassword())).thenReturn(true);

            // When
            var result = authService.login(loginRequest);

            // Then
            assertNotNull(result);
            verify(userRepository, times(1)).findByEmailId("user@example.com");
            verify(passwordEncoder, times(1)).matches("SecureP@1", existingUser.getPassword());
        }
    }

    @Nested
    @DisplayName("Successful Login Tests")
    class SuccessfulLoginTests {

        @Test
        @DisplayName("Should login successfully with valid credentials and return LoginResponseDto")
        void shouldLoginSuccessfullyWithValidCredentials() {
            // Given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("user@example.com");
            loginRequest.setPassword("SecureP@1");

            User existingUser = new User(1L, "johndoe", "user@example.com", "SecureP@1", Role.USER);
            when(userRepository.findByEmailId("user@example.com")).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches("SecureP@1", existingUser.getPassword())).thenReturn(true);

            // When
            LoginResponseDto result = authService.login(loginRequest);

            // Then
            assertNotNull(result);
            assertEquals("user@example.com", result.getEmail());
            assertEquals(Role.USER, result.getRole());
            assertNotNull(result.getAccessToken());
            assertFalse(result.getAccessToken().isEmpty());
            verify(userRepository, times(1)).findByEmailId("user@example.com");
            verify(passwordEncoder, times(1)).matches("SecureP@1", existingUser.getPassword());
        }

        @Test
        @DisplayName("Should login successfully for ADMIN user and return LoginResponseDto")
        void shouldLoginSuccessfullyForAdminUser() {
            // Given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("admin@example.com");
            loginRequest.setPassword("AdminP@1");

            User adminUser = new User(2L, "admin", "admin@example.com", "AdminP@1", Role.ADMIN);
            when(userRepository.findByEmailId("admin@example.com")).thenReturn(Optional.of(adminUser));
            when(passwordEncoder.matches("AdminP@1", adminUser.getPassword())).thenReturn(true);

            // When
            LoginResponseDto result = authService.login(loginRequest);

            // Then
            assertNotNull(result);
            assertEquals("admin@example.com", result.getEmail());
            assertEquals(Role.ADMIN, result.getRole());
            assertNotNull(result.getAccessToken());
            assertFalse(result.getAccessToken().isEmpty());
            verify(userRepository, times(1)).findByEmailId("admin@example.com");
            verify(passwordEncoder, times(1)).matches("AdminP@1", adminUser.getPassword());
        }

        @Test
        @DisplayName("Should login successfully for USER role and return LoginResponseDto")
        void shouldLoginSuccessfullyForUserRole() {
            // Given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("regular@example.com");
            loginRequest.setPassword("UserP@ss1");

            User regularUser = new User(3L, "regularuser", "regular@example.com", "UserP@ss1", Role.USER);
            when(userRepository.findByEmailId("regular@example.com")).thenReturn(Optional.of(regularUser));
            when(passwordEncoder.matches("UserP@ss1", regularUser.getPassword())).thenReturn(true);

            // When
            LoginResponseDto result = authService.login(loginRequest);

            // Then
            assertNotNull(result);
            assertEquals("regular@example.com", result.getEmail());
            assertEquals(Role.USER, result.getRole());
            assertNotNull(result.getAccessToken());
            assertFalse(result.getAccessToken().isEmpty());
            verify(userRepository, times(1)).findByEmailId("regular@example.com");
            verify(passwordEncoder, times(1)).matches("UserP@ss1", regularUser.getPassword());
        }
    }

    @Nested
    @DisplayName("Integration Validation Tests")
    class IntegrationValidationTests {

        @Test
        @DisplayName("Should validate email before checking repository")
        void shouldValidateEmailBeforeCheckingRepository() {
            // Given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("invalid-email");
            loginRequest.setPassword("SecureP@1");

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                authService.login(loginRequest);
            });
            verify(userRepository, never()).findByEmailId(anyString());
        }

        @Test
        @DisplayName("Should validate password before checking repository")
        void shouldValidatePasswordBeforeCheckingRepository() {
            // Given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("user@example.com");
            loginRequest.setPassword("short");

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                authService.login(loginRequest);
            });
            verify(userRepository, never()).findByEmailId(anyString());
        }

        @Test
        @DisplayName("Should validate both email and password before repository lookup")
        void shouldValidateBothEmailAndPasswordBeforeRepositoryLookup() {
            // Given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail(null);
            loginRequest.setPassword(null);

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                authService.login(loginRequest);
            });
            verify(userRepository, never()).findByEmailId(anyString());
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }
    }
}
