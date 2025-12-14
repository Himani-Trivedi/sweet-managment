package com.api.mithai.base.exception;

import com.api.mithai.base.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    public void errorMessageLog(Exception ex, HttpServletRequest httpServletRequest) {
        log.error("Error at {} while processing {} {}: {}",
                LocalDateTime.now(ZoneOffset.UTC),
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                ex.getMessage(), ex);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<BaseResponse> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest httpServletRequest) {
        errorMessageLog(ex, httpServletRequest);

        HttpStatus status = ex.getStatus() != null ? ex.getStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
        BaseResponse errorResponse = new BaseResponse(false, ex.getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse> handleArgumentException(IllegalArgumentException ex, HttpServletRequest httpServletRequest) {
        errorMessageLog(ex, httpServletRequest);

        BaseResponse errorResponse = new BaseResponse(false, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest httpServletRequest) {
        errorMessageLog(ex, httpServletRequest);

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        BaseResponse errorResponse = new BaseResponse(false, errorMessage);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest httpServletRequest) {
        errorMessageLog(ex, httpServletRequest);

        BaseResponse errorResponse = new BaseResponse(false, "You are not allowed to access this resource");
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<BaseResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex, HttpServletRequest httpServletRequest) {
        errorMessageLog(ex, httpServletRequest);

        BaseResponse errorResponse = new BaseResponse(false, "You are not authorized to access this resource");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleException(Exception ex, HttpServletRequest httpServletRequest) {
        errorMessageLog(ex, httpServletRequest);

        BaseResponse errorResponse = new BaseResponse(false, "Something went wrong.");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}

