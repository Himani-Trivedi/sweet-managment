package com.api.mithai.auth.service;

import com.api.mithai.auth.dto.LoginRequestDto;
import com.api.mithai.auth.dto.LoginResponseDto;
import com.api.mithai.auth.dto.RegisterAuthRequestDto;
import com.api.mithai.auth.entity.User;
import com.api.mithai.auth.enums.Role;
import com.api.mithai.auth.repository.UserRepository;
import com.api.mithai.base.exception.ResponseStatusException;
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

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        if (!validateEmail(loginRequestDto.getEmail()) || !validatePassword(loginRequestDto.getPassword())) {
            throw new ResponseStatusException("Invalid email or password", HttpStatus.BAD_REQUEST);
        }

        Optional<User> user = userRepository.findByEmailId(loginRequestDto.getEmail());
        if (user.isEmpty()) {
            throw new ResponseStatusException("Invalid email or password", HttpStatus.BAD_REQUEST);
        }

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.get().getPassword())) {
            throw new ResponseStatusException("Invalid email or password", HttpStatus.BAD_REQUEST);
        }

        return new LoginResponseDto(user.get().getEmailId()
                , "accessTOken"
                , user.get().getRoleName());
    }

    public void register(RegisterAuthRequestDto registerAuthRequestDto) {
        if (!validateEmail(registerAuthRequestDto.getEmailId()) || !validatePassword(registerAuthRequestDto.getPassword())) {
            throw new ResponseStatusException("Invalid email or password", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmailId(registerAuthRequestDto.getEmailId())) {
            throw new ResponseStatusException("User with this email already exists", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setEmailId(registerAuthRequestDto.getEmailId());
        user.setPassword(registerAuthRequestDto.getPassword());
        user.setUsername(registerAuthRequestDto.getEmailId().substring(0,5));
        user.setRoleName(Role.USER);
        userRepository.save(user);
    }

    public boolean validateEmail(String emailId) {
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

        return true;
    }

    public boolean validatePassword(String password) {
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

}
