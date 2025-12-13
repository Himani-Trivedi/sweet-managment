package com.api.mithai.auth.dto;

import com.api.mithai.auth.enums.Role;
import lombok.Data;

@Data
public class LoginResponseDto {
    private String email;
    private String accessToken;
    private Role role;
}
