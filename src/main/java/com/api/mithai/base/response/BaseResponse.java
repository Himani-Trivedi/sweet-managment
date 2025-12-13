package com.api.mithai.base.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponse <T> {
    public String message;
    private Boolean status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public T data;

    public BaseResponse(T data, Boolean status, String message) {
        this.data = data;
        this.status = status;
        this.message = message;
    }

    public BaseResponse(Boolean status, String message) {
        this.status = status;
        this.message = message;
    }
}
