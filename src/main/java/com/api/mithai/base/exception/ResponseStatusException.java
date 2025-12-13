package com.api.mithai.base.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ResponseStatusException extends RuntimeException {
    private HttpStatus status = null;
    private Object data = null;

    public ResponseStatusException(String message) {
        super(message);
    }

    public ResponseStatusException(String message, HttpStatus status) {
        this(message);
        this.status = status;
    }

    public ResponseStatusException(String message, HttpStatus status, Object data) {
        this(message, status);
        this.data = data;
    }
}
