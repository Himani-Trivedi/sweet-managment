package com.api.mithai.auth.dto;

import com.api.mithai.auth.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    private String email;
    private String accessToken;
    private Role role;
}
