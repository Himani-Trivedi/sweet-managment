package com.api.mithai.auth.entity;

import com.api.mithai.auth.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username cannot be null or empty")
    @Pattern(regexp = "^\\S+$", message = "Username cannot be blank")
    @Column(nullable = false)
    private String username;

    @NotBlank(message = "Email ID cannot be null or empty")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email ID format is invalid")
    @Column(nullable = false, unique = true)
    private String emailId;

    @NotBlank(message = "Password cannot be null or empty")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[^A-Za-z0-9]).{8,12}$",
             message = "Password must be between 8 and 12 characters and contain at least one uppercase, one lowercase, and one special character")
    @Column(nullable = false)
    private String password; // Stored as hash

    @NotNull(message = "Role name cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role roleName;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User(Long id, String username, String emailId, String password, Role roleName) {
        // Validate username
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        String trimmedUsername = username.trim();
        if (trimmedUsername.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (!trimmedUsername.matches("^\\S+$")) {
            throw new IllegalArgumentException("Username cannot be blank");
        }

        // Validate emailId
        if (emailId == null) {
            throw new IllegalArgumentException("Email ID cannot be null");
        }
        String trimmedEmail = emailId.trim();
        if (trimmedEmail.isEmpty()) {
            throw new IllegalArgumentException("Email ID cannot be empty");
        }
        if (!trimmedEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Email ID format is invalid");
        }

        // Validate password
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        if (password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (!password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*[^A-Za-z0-9]).{8,12}$")) {
            if (password.length() < 8 || password.length() > 12) {
                throw new IllegalArgumentException("Password must be between 8 and 12 characters");
            }
            if (!password.matches(".*[A-Z].*")) {
                throw new IllegalArgumentException("Password must contain at least one uppercase letter");
            }
            if (!password.matches(".*[a-z].*")) {
                throw new IllegalArgumentException("Password must contain at least one lowercase letter");
            }
            if (!password.matches(".*[^A-Za-z0-9].*")) {
                throw new IllegalArgumentException("Password must contain at least one special character");
            }
        }

        // Validate roleName
        if (roleName == null) {
            throw new IllegalArgumentException("Role name cannot be null");
        }

        if(!roleName.equals(Role.USER) && !roleName.equals(Role.ADMIN)){
            throw new IllegalArgumentException("Role name cannot be other than USER & ADMIN");
        }

        this.id = id;
        this.username = trimmedUsername;
        this.emailId = trimmedEmail;
        this.password = hashPassword(password);
        this.roleName = roleName;
    }

    private String hashPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        String trimmedUsername = username.trim();
        if (trimmedUsername.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (!trimmedUsername.matches("^\\S+$")) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        this.username = trimmedUsername;
    }

    public void setEmailId(String emailId) {
        // Validate emailId
        if (emailId == null) {
            throw new IllegalArgumentException("Email ID cannot be null");
        }
        String trimmedEmail = emailId.trim();
        if (trimmedEmail.isEmpty()) {
            throw new IllegalArgumentException("Email ID cannot be empty");
        }
        if (!trimmedEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Email ID format is invalid");
        }
        this.emailId = trimmedEmail;
    }

    public void setPassword(String password) {
        // Validate password
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        if (password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (!password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*[^A-Za-z0-9]).{8,12}$")) {
            if (password.length() < 8 || password.length() > 12) {
                throw new IllegalArgumentException("Password must be between 8 and 12 characters");
            }
            if (!password.matches(".*[A-Z].*")) {
                throw new IllegalArgumentException("Password must contain at least one uppercase letter");
            }
            if (!password.matches(".*[a-z].*")) {
                throw new IllegalArgumentException("Password must contain at least one lowercase letter");
            }
            if (!password.matches(".*[^A-Za-z0-9].*")) {
                throw new IllegalArgumentException("Password must contain at least one special character");
            }
        }
        this.password = hashPassword(password);
    }

    public void setRoleName(Role roleName) {
        // Validate roleName
        if (roleName == null) {
            throw new IllegalArgumentException("Role name cannot be null");
        }
        if (!roleName.equals(Role.USER) && !roleName.equals(Role.ADMIN)) {
            throw new IllegalArgumentException("Role name cannot be other than USER & ADMIN");
        }
        this.roleName = roleName;
    }
}
