package com.api.mithai.auth.service;

import com.api.mithai.auth.dto.LoginRequestDto;
import com.api.mithai.auth.dto.LoginResponseDto;
import com.api.mithai.auth.dto.RegisterAuthRequestDto;
import com.api.mithai.auth.entity.User;
import com.api.mithai.auth.enums.Role;
import com.api.mithai.auth.repository.UserRepository;
import com.api.mithai.base.exception.ResponseStatusException;
import com.api.mithai.security.AccessTokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenManager accessTokenManager;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        String emailId = validateEmail(loginRequestDto.getEmail());
        String password = loginRequestDto.getPassword();

        if (!validatePassword(password)) {
            throw new ResponseStatusException("Invalid email or password", HttpStatus.BAD_REQUEST);
        }

        Optional<User> user = userRepository.findByEmailId(emailId);
        if (user.isEmpty()) {
            throw new ResponseStatusException("Invalid email or password", HttpStatus.BAD_REQUEST);
        }

        if (!passwordEncoder.matches(password, user.get().getPassword())) {
            throw new ResponseStatusException("Invalid email or password", HttpStatus.BAD_REQUEST);
        }

        return new LoginResponseDto(user.get().getEmailId()
                , accessTokenManager.getAccessToken(user.get())
                , user.get().getRoleName());
    }

    public void register(RegisterAuthRequestDto registerAuthRequestDto) {
        String emailId = validateEmail(registerAuthRequestDto.getEmailId());
        String password = registerAuthRequestDto.getPassword();

        if (!validatePassword(password)) {
            throw new ResponseStatusException("Invalid email or password", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmailId(emailId)) {
            throw new ResponseStatusException("User with this email already exists", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setEmailId(emailId);
        user.setPassword(password);
        user.setUsername(emailId.substring(0,5));
        user.setRoleName(Role.USER);
        userRepository.save(user);
    }

    public static String validateEmail(String emailId) {
        if (emailId == null) {
            throw new IllegalArgumentException("Email ID cannot be null");
        }

        if (emailId.isEmpty()) {
            throw new IllegalArgumentException("Email ID cannot be empty");
        }

        String trimmedEmail = emailId.trim();
        if (trimmedEmail.isEmpty()) {
            throw new IllegalArgumentException("Email ID cannot be blank");
        }
        if (!trimmedEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Email ID format is invalid");
        }

        return trimmedEmail;
    }

    public static boolean validatePassword(String password) {
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
        return true;
    }

    public static String validateUsername(String username) {
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
        return trimmedUsername;
    }

    public static boolean validateRoleName(Role roleName) {
        // Validate roleName
        if (roleName == null) {
            throw new IllegalArgumentException("Role name cannot be null");
        }
        if (!roleName.equals(Role.USER) && !roleName.equals(Role.ADMIN)) {
            throw new IllegalArgumentException("Role name cannot be other than USER & ADMIN");
        }
        return true;
    }

}
