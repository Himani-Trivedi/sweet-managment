package com.api.mithai.sweet.controller;

import com.api.mithai.base.constants.Constants;
import com.api.mithai.base.constants.Urls;
import com.api.mithai.base.response.BaseResponse;
import com.api.mithai.base.response.ResponseHandler;
import com.api.mithai.sweet.service.SweetCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(Urls.BASE_URL + Urls.SWEETS_URL + Urls.CATEGORY_URL)
public class SweetCategoryController {

    private final SweetCategoryService sweetCategoryService;
    private final ResponseHandler responseHandler;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse> getAllCategories() {
        return responseHandler.okResponse(sweetCategoryService.getAllCategories(), HttpStatus.OK, Constants.SUCCESS);
    }
}
