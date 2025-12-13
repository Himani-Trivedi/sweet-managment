package com.api.mithai.auth.controller;

import com.api.mithai.auth.dto.LoginRequestDto;
import com.api.mithai.base.constants.Constants;
import com.api.mithai.auth.dto.RegisterAuthRequestDto;
import com.api.mithai.auth.service.AuthService;
import com.api.mithai.base.constants.Urls;
import com.api.mithai.base.response.BaseResponse;
import com.api.mithai.base.response.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Urls.BASE_URL + Urls.AUTH_URL)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ResponseHandler responseHandler;

    @PostMapping(Urls.REGISTER_URL)
    public ResponseEntity<BaseResponse> register(@RequestBody @Valid RegisterAuthRequestDto registerAuthRequestDto) {
        authService.register(registerAuthRequestDto);
        return responseHandler.okResponse(HttpStatus.CREATED, Constants.USER_CREATED_SUCCESSFULLY);
    }

    @PostMapping(Urls.LOGIN_URL)
    public ResponseEntity<BaseResponse> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        return responseHandler.okResponse(authService.login(loginRequestDto), HttpStatus.OK, Constants.LOGIN_SUCCESSFUL);
    }
}