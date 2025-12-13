package com.api.mithai.auth.entity;

import com.api.mithai.auth.enums.Role;
import com.api.mithai.auth.service.AuthService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String emailId;

    @Column(nullable = false)
    private String password; // Stored as hash

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role roleName;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User(Long id, String username, String emailId, String password, Role roleName) {
        this.id = id;
        this.username = AuthService.validateUsername(username);
        this.emailId = AuthService.validateEmail(emailId);

        AuthService.validatePassword(password);
        this.password = hashPassword(password);

        AuthService.validateRoleName(roleName);
        this.roleName = roleName;
    }

    private String hashPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username =AuthService.validateUsername(username);
    }

    public void setEmailId(String emailId) {
        this.emailId = AuthService.validateEmail(emailId);;
    }

    public void setPassword(String password) {
        AuthService.validatePassword(password);
        this.password = hashPassword(password);
    }

    public void setRoleName(Role roleName) {
        AuthService.validateRoleName(roleName);
        this.roleName = roleName;
    }
}
