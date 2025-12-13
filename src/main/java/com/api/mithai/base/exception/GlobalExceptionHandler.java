package com.api.mithai.base.exception;

import com.api.mithai.base.response.BaseResponse;
import com.api.mithai.base.response.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
public class GlobalExceptionHandler {

    private final ResponseHandler responseHandler;

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<BaseResponse> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatus status = ex.getStatus() != null ? ex.getStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
        BaseResponse errorResponse = new BaseResponse(false, ex.getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }
}

