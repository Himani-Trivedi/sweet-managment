package com.api.mithai.sweet.controller;

import com.api.mithai.base.constants.Constants;
import com.api.mithai.base.constants.Urls;
import com.api.mithai.base.response.BaseResponse;
import com.api.mithai.base.response.PaginatedBaseResponse;
import com.api.mithai.base.response.ResponseHandler;
import com.api.mithai.sweet.dto.SweetRequestDto;
import com.api.mithai.sweet.dto.SweetResponseDto;
import com.api.mithai.sweet.service.SweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(Urls.BASE_URL + Urls.SWEETS_URL)
@RequiredArgsConstructor
public class SweetController {

    private final SweetService sweetService;
    private final ResponseHandler responseHandler;

    @PostMapping
    public ResponseEntity<BaseResponse> create(@RequestBody @Valid SweetRequestDto sweetRequestDto) {
        SweetResponseDto sweetResponseDto = sweetService.create(sweetRequestDto);
        return responseHandler.okResponse(sweetResponseDto, HttpStatus.CREATED, Constants.SWEET_CREATED_SUCCESSFULLY);
    }

    @GetMapping
    public ResponseEntity<BaseResponse> listAll(@RequestParam Map<String, Object> params) {
        PaginatedBaseResponse<SweetResponseDto> paginatedResponse = sweetService.listAll(params);
        return responseHandler.okResponse(paginatedResponse, HttpStatus.OK, Constants.SWEETS_RETRIEVED_SUCCESSFULLY);
    }

    @GetMapping(Urls.SEARCH_URL)
    public ResponseEntity<BaseResponse> search(
            @RequestParam(required = false) String searchValue,
            @RequestParam(required = false) Double minValue,
            @RequestParam(required = false) Double maxValue,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        
        Map<String, Object> params = new HashMap<>();
        if (searchValue != null) params.put("searchValue", searchValue);
        if (minValue != null) params.put("minValue", minValue);
        if (maxValue != null) params.put("maxValue", maxValue);
        if (page != null) params.put("page", page);
        if (size != null) params.put("size", size);
        if (sortField != null) params.put("sortField", sortField);
        if (sortOrder != null) params.put("sortOrder", sortOrder);
        
        PaginatedBaseResponse<SweetResponseDto> paginatedResponse = sweetService.listAll(params);
        return responseHandler.okResponse(paginatedResponse, HttpStatus.OK, Constants.SWEETS_RETRIEVED_SUCCESSFULLY);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid SweetRequestDto sweetRequestDto) {
        SweetResponseDto sweetResponseDto = sweetService.update(id, sweetRequestDto);
        return responseHandler.okResponse(sweetResponseDto, HttpStatus.OK, Constants.SWEET_UPDATED_SUCCESSFULLY);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> delete(@PathVariable Long id) {
        sweetService.delete(id);
        return responseHandler.okResponse(HttpStatus.OK, Constants.SWEET_DELETED_SUCCESSFULLY);
    }
}

