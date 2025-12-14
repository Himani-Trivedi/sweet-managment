package com.api.mithai.sweet.controller;

import com.api.mithai.base.constants.Constants;
import com.api.mithai.base.constants.Urls;
import com.api.mithai.base.response.BaseResponse;
import com.api.mithai.base.response.PaginatedBaseResponse;
import com.api.mithai.base.response.ResponseHandler;
import com.api.mithai.sweet.dto.SweetRequestDto;
import com.api.mithai.sweet.dto.SweetResponseDto;
import com.api.mithai.sweet.service.SweetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(Urls.BASE_URL + Urls.SWEETS_URL)
@RequiredArgsConstructor
@Tag(name = "Sweets Management", description = "APIs for managing sweets (CRUD operations)")
public class SweetController {

    private final SweetService sweetService;
    private final ResponseHandler responseHandler;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new sweet", description = "Add a new sweet to the inventory (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<BaseResponse> create(@RequestBody @Valid SweetRequestDto sweetRequestDto) {
        SweetResponseDto sweetResponseDto = sweetService.create(sweetRequestDto);
        return responseHandler.okResponse(sweetResponseDto, HttpStatus.CREATED, Constants.SWEET_CREATED_SUCCESSFULLY);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all sweets", description = "Get a paginated list of all available sweets (Public access)")
    public ResponseEntity<BaseResponse> listAll(@RequestParam Map<String, Object> params) {
        PaginatedBaseResponse<SweetResponseDto> paginatedResponse = sweetService.listAll(params);
        return responseHandler.okResponse(paginatedResponse, HttpStatus.OK, Constants.SWEETS_RETRIEVED_SUCCESSFULLY);
    }

    @GetMapping(Urls.SEARCH_URL)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Search sweets", description = "Search sweets by name, category, or price range (Public access)")
    public ResponseEntity<BaseResponse> search(@RequestParam Map<String, Object> params) {
        PaginatedBaseResponse<SweetResponseDto> paginatedResponse = sweetService.listAll(params);
        return responseHandler.okResponse(paginatedResponse, HttpStatus.OK, Constants.SWEETS_RETRIEVED_SUCCESSFULLY);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a sweet", description = "Update sweet details by ID (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<BaseResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid SweetRequestDto sweetRequestDto) {
        SweetResponseDto sweetResponseDto = sweetService.update(id, sweetRequestDto);
        return responseHandler.okResponse(sweetResponseDto, HttpStatus.OK, Constants.SWEET_UPDATED_SUCCESSFULLY);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a sweet", description = "Delete a sweet by ID (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<BaseResponse> delete(@PathVariable Long id) {
        sweetService.delete(id);
        return responseHandler.okResponse(HttpStatus.OK, Constants.SWEET_DELETED_SUCCESSFULLY);
    }
}

