package com.api.mithai.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterAuthRequestDto {
    @NotBlank
    private String emailId;

    @NotBlank
    private String password;
}

