package com.api.mithai.base.response;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResponseHandler {

    public <T> ResponseEntity<BaseResponse> okResponse(T t, HttpStatus statusCode, String message) {
        BaseResponse baseResponse = new BaseResponse(t, true, message);
        return new ResponseEntity<>(baseResponse, statusCode);
    }

    public <T> ResponseEntity<BaseResponse> okResponse(HttpStatus statusCode, String message) {
        BaseResponse baseResponse = new BaseResponse(true,message);
        return new ResponseEntity<>(baseResponse, statusCode);
    }
}
