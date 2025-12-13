package com.api.mithai.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Domain tests for User entity following TDD approach
 */
@DisplayName("User Domain Tests")
class UserTest {

    @Nested
    @DisplayName("User Creation Tests")
    class UserCreationTests {

        @Test
        @DisplayName("Should create user with valid data")
        void shouldCreateUserWithValidData() {
            // Given
            Long id = 1L;
            String username = "johndoe";
            String emailId = "user@example.com";
            String password = "SecureP@1";
            Role roleName = Role.USER;

            // When
            User user = new User(id, username, emailId, password, roleName);

            // Then
            assertNotNull(user);
            assertEquals(id, user.getId());
            assertEquals(username, user.getUsername());
            assertEquals(emailId, user.getEmailId());
            assertNotNull(user.getPassword()); // Password should be stored (as hash)
            assertEquals(roleName, user.getRoleName());
        }

        @Test
        @DisplayName("Should create user with null ID for new entities")
        void shouldCreateUserWithNullId() {
            // Given
            Long id = null;
            String username = "johndoe";
            String emailId = "user@example.com";
            String password = "SecureP@1";
            Role roleName = Role.USER;

            // When & Then
            assertDoesNotThrow(() -> {
                User user = new User(id, username, emailId, password, roleName);
                assertNull(user.getId());
            }, "Null ID should be allowed for new entities");
        }
    }

    @Nested
    @DisplayName("Username Validation Tests")
    class UsernameValidationTests {

        @Test
        @DisplayName("Should throw exception when username is null")
        void shouldThrowExceptionWhenUsernameIsNull() {
            // Given
            Long id = 1L;
            String username = null;
            String emailId = "user@example.com";
            String password = "SecureP@1";
            Role roleName = Role.USER;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new User(id, username, emailId, password, roleName);
            }, "Username cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when username is empty")
        void shouldThrowExceptionWhenUsernameIsEmpty() {
            // Given
            Long id = 1L;
            String username = "";
            String emailId = "user@example.com";
            String password = "SecureP@1";
            Role roleName = Role.USER;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new User(id, username, emailId, password, roleName);
            }, "Username cannot be empty");
        }

        @Test
        @DisplayName("Should throw exception when username is blank")
        void shouldThrowExceptionWhenUsernameIsBlank() {
            // Given
            Long id = 1L;
            String username = "   ";
            String emailId = "user@example.com";
            String password = "SecureP@1";
            Role roleName = Role.USER;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new User(id, username, emailId, password, roleName);
            }, "Username cannot be blank");
        }

        @Test
        @DisplayName("Should trim trailing spaces from username")
        void shouldTrimTrailingSpacesFromUsername() {
            // Given
            Long id = 1L;
            String usernameWithSpaces = "johndoe   ";
            String emailId = "user@example.com";
            String password = "SecureP@1";
            Role roleName = Role.USER;

            // When
            User user = new User(id, usernameWithSpaces, emailId, password, roleName);

            // Then
            assertEquals("johndoe", user.getUsername());
        }

        @Test
        @DisplayName("Should trim leading spaces from username")
        void shouldTrimLeadingSpacesFromUsername() {
            // Given
            Long id = 1L;
            String usernameWithSpaces = "   johndoe";
            String emailId = "user@example.com";
            String password = "SecureP@1";
            Role roleName = Role.USER;

            // When
            User user = new User(id, usernameWithSpaces, emailId, password, roleName);

            // Then
            assertEquals("johndoe", user.getUsername());
        }

        @Test
        @DisplayName("Should accept valid usernames")
        void shouldAcceptValidUsernames() {
            String[] validUsernames = {
                "johndoe",
                "john_doe",
                "john123",
                "JohnDoe",
                "user123"
            };

            for (String username : validUsernames) {
                // When & Then
                assertDoesNotThrow(() -> {
                    new User(1L, username, "user@example.com", "SecureP@1", Role.USER);
                }, "Username " + username + " should be valid");
            }
        }
    }

    @Nested
    @DisplayName("Email Validation Tests")
    class EmailValidationTests {

        @Test
        @DisplayName("Should throw exception when emailId is null")
        void shouldThrowExceptionWhenEmailIdIsNull() {
            // Given
            Long id = 1L;
            String username = "johndoe";
            String emailId = null;
            String password = "SecureP@1";
            Role roleName = Role.USER;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new User(id, username, emailId, password, roleName);
            }, "Email ID cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when emailId is empty")
        void shouldThrowExceptionWhenEmailIdIsEmpty() {
            // Given
            Long id = 1L;
            String username = "johndoe";
            String emailId = "";
            String password = "SecureP@1";
            Role roleName = Role.USER;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new User(id, username, emailId, password, roleName);
            }, "Email ID cannot be empty");
        }

        @Test
        @DisplayName("Should throw exception when emailId is blank")
        void shouldThrowExceptionWhenEmailIdIsBlank() {
            // Given
            Long id = 1L;
            String username = "johndoe";
            String emailId = "   ";
            String password = "SecureP@1";
            Role roleName = Role.USER;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new User(id, username, emailId, password, roleName);
            }, "Email ID cannot be blank");
        }

        @Test
        @DisplayName("Should throw exception when emailId format is invalid")
        void shouldThrowExceptionWhenEmailIdFormatIsInvalid() {
            // Given
            Long id = 1L;
            String username = "johndoe";
            String emailId = "invalid-email";
            String password = "SecureP@1";
            Role roleName = Role.USER;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new User(id, username, emailId, password, roleName);
            }, "Email ID format is invalid");
        }

        @Test
        @DisplayName("Should accept valid email formats")
        void shouldAcceptValidEmailFormats() {
            String[] validEmails = {
                "user@example.com",
                "test.user@example.co.uk",
                "user123@test-domain.com",
                "firstname.lastname@company.com",
                "user+tag@example.com"
            };

            for (String email : validEmails) {
                // When & Then
                assertDoesNotThrow(() -> {
                    new User(1L, "johndoe", email, "SecureP@1", Role.USER);
                }, "Email " + email + " should be valid");
            }
        }

        @Test
        @DisplayName("Should reject invalid email formats")
        void shouldRejectInvalidEmailFormats() {
            String[] invalidEmails = {
                "invalid-email",
                "@example.com",
                "user@",
                "user@.com",
                "user name@example.com",
                "user@exam ple.com",
                "user@example",
                "user@@example.com"
            };

            for (String email : invalidEmails) {
                // When & Then
                assertThrows(IllegalArgumentException.class, () -> {
                    new User(1L, "johndoe", email, "SecureP@1", Role.USER);
                }, "Email " + email + " should be invalid");
            }
        }
    }

    @Nested
    @DisplayName("Password Validation Tests")
    class PasswordValidationTests {

        @Test
        @DisplayName("Should throw exception when password is null")
        void shouldThrowExceptionWhenPasswordIsNull() {
            // Given
            Long id = 1L;
            String username = "johndoe";
            String emailId = "user@example.com";
            String password = null;
            Role roleName = Role.USER;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new User(id, username, emailId, password, roleName);
            }, "Password cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when password is empty")
        void shouldThrowExceptionWhenPasswordIsEmpty() {
            // Given
            Long id = 1L;
            String username = "johndoe";
            String emailId = "user@example.com";
            String password = "";
            Role roleName = Role.USER;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new User(id, username, emailId, password, roleName);
            }, "Password cannot be empty");
        }

        @Test
        @DisplayName("Should throw exception when password is too short")
        void shouldThrowExceptionWhenPasswordIsTooShort() {
            // Given
            Long id = 1L;
            String username = "johndoe";
            String emailId = "user@example.com";
            String password = "Short1@";
            Role roleName = Role.USER;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new User(id, username, emailId, password, roleName);
            }, "Password must be between 8 and 12 characters");
        }

        @Test
        @DisplayName("Should throw exception when password is too long")
        void shouldThrowExceptionWhenPasswordIsTooLong() {
            // Given
            Long id = 1L;
            String username = "johndoe";
            String emailId = "user@example.com";
            String password = "VeryLongPassword123@";
            Role roleName = Role.USER;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new User(id, username, emailId, password, roleName);
            }, "Password must be between 8 and 12 characters");
        }

        @Test
        @DisplayName("Should throw exception when password lacks uppercase letter")
        void shouldThrowExceptionWhenPasswordLacksUppercase() {
            // Given
            Long id = 1L;
            String username = "johndoe";
            String emailId = "user@example.com";
            String password = "securep@1";
            Role roleName = Role.USER;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new User(id, username, emailId, password, roleName);
            }, "Password must contain at least one uppercase letter");
        }

        @Test
        @DisplayName("Should throw exception when password lacks lowercase letter")
        void shouldThrowExceptionWhenPasswordLacksLowercase() {
            // Given
            Long id = 1L;
            String username = "johndoe";
            String emailId = "user@example.com";
            String password = "SECUREP@1";
            Role roleName = Role.USER;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new User(id, username, emailId, password, roleName);
            }, "Password must contain at least one lowercase letter");
        }

        @Test
        @DisplayName("Should throw exception when password lacks special character")
        void shouldThrowExceptionWhenPasswordLacksSpecialCharacter() {
            // Given
            Long id = 1L;
            String username = "johndoe";
            String emailId = "user@example.com";
            String password = "SecureP12";
            Role roleName = Role.USER;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new User(id, username, emailId, password, roleName);
            }, "Password must contain at least one special character");
        }

        @Test
        @DisplayName("Should accept valid passwords")
        void shouldAcceptValidPasswords() {
            String[] validPasswords = {
                "SecureP@1",      // 9 chars: uppercase, lowercase, special
                "MyP@ssw0rd",     // 10 chars: uppercase, lowercase, special
                "Test1234@",      // 9 chars: uppercase, lowercase, special
                "Pass@1234",      // 9 chars: uppercase, lowercase, special
                "Abc123!@#",      // 9 chars: uppercase, lowercase, special
                "ValidP@ss1",     // 10 chars: uppercase, lowercase, special
                "SecureP@12"     // 10 chars: uppercase, lowercase, special
            };

            for (String password : validPasswords) {
                // When & Then
                assertDoesNotThrow(() -> {
                    new User(1L, "johndoe", "user@example.com", password, Role.USER);
                }, "Password " + password + " should be valid");
            }
        }

        @Test
        @DisplayName("Should accept password with exactly 8 characters")
        void shouldAcceptPasswordWithExactly8Characters() {
            // Given
            Long id = 1L;
            String username = "johndoe";
            String emailId = "user@example.com";
            String password = "Secure@1";
            Role roleName = Role.USER;

            // When & Then
            assertDoesNotThrow(() -> {
                new User(id, username, emailId, password, roleName);
            }, "Password with 8 characters should be valid");
        }

        @Test
        @DisplayName("Should accept password with exactly 12 characters")
        void shouldAcceptPasswordWithExactly12Characters() {
            // Given
            Long id = 1L;
            String username = "johndoe";
            String emailId = "user@example.com";
            String password = "SecureP@123";
            Role roleName = Role.USER;

            // When & Then
            assertDoesNotThrow(() -> {
                new User(id, username, emailId, password, roleName);
            }, "Password with 12 characters should be valid");
        }
    }

    @Nested
    @DisplayName("Role Validation Tests")
    class RoleValidationTests {

        @Test
        @DisplayName("Should throw exception when roleName is null")
        void shouldThrowExceptionWhenRoleNameIsNull() {
            // Given
            Long id = 1L;
            String username = "johndoe";
            String emailId = "user@example.com";
            String password = "SecureP@1";
            Role roleName = null;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new User(id, username, emailId, password, roleName);
            }, "Role name cannot be null");
        }

        @Test
        @DisplayName("Should accept USER role")
        void shouldAcceptUserRole() {
            // Given
            Long id = 1L;
            String username = "johndoe";
            String emailId = "user@example.com";
            String password = "SecureP@1";
            Role roleName = Role.USER;

            // When
            User user = new User(id, username, emailId, password, roleName);

            // Then
            assertEquals(Role.USER, user.getRoleName());
        }

        @Test
        @DisplayName("Should accept ADMIN role")
        void shouldAcceptAdminRole() {
            // Given
            Long id = 1L;
            String username = "johndoe";
            String emailId = "user@example.com";
            String password = "SecureP@1";
            Role roleName = Role.ADMIN;

            // When
            User user = new User(id, username, emailId, password, roleName);

            // Then
            assertEquals(Role.ADMIN, user.getRoleName());
        }
    }

    @Nested
    @DisplayName("Password Hashing Tests")
    class PasswordHashingTests {

        @Test
        @DisplayName("Should store password in hash format")
        void shouldStorePasswordInHashFormat() {
            // Given
            Long id = 1L;
            String username = "johndoe";
            String emailId = "user@example.com";
            String plainPassword = "SecureP@1";
            Role roleName = Role.USER;

            // When
            User user = new User(id, username, emailId, plainPassword, roleName);

            // Then
            assertNotNull(user.getPassword());
            assertNotEquals(plainPassword, user.getPassword(), "Password should be hashed, not stored as plain text");
            assertTrue(user.getPassword().length() > plainPassword.length(), "Hashed password should be longer than plain password");
        }
    }
}
