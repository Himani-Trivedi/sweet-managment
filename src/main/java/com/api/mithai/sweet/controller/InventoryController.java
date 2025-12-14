package com.api.mithai.sweet.controller;

import com.api.mithai.base.constants.Constants;
import com.api.mithai.base.constants.Urls;
import com.api.mithai.base.response.BaseResponse;
import com.api.mithai.base.response.ResponseHandler;
import com.api.mithai.sweet.dto.PurchaseDto;
import com.api.mithai.sweet.dto.RestockDto;
import com.api.mithai.sweet.dto.SweetResponseDto;
import com.api.mithai.sweet.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Urls.BASE_URL + Urls.SWEETS_URL)
@RequiredArgsConstructor
@Tag(name = "Inventory Management", description = "APIs for managing sweet inventory (purchase and restock)")
public class InventoryController {

    private final InventoryService inventoryService;
    private final ResponseHandler responseHandler;

    @PostMapping("/{id}" + Urls.PURCHASE_URL)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Purchase a sweet", description = "Purchase a sweet, decreasing its quantity (User role required)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<BaseResponse> purchase(
            @PathVariable Long id,
            @RequestBody @Valid PurchaseDto purchaseDto) {
        SweetResponseDto sweetResponseDto = inventoryService.purchase(id, purchaseDto);
        return responseHandler.okResponse(sweetResponseDto, HttpStatus.OK, Constants.SWEET_PURCHASED_SUCCESSFULLY);
    }

    @PostMapping("/{id}" + Urls.RESTOCK_URL)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Restock a sweet", description = "Restock a sweet, increasing its quantity (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<BaseResponse> restock(
            @PathVariable Long id,
            @RequestBody @Valid RestockDto restockDto) {
        SweetResponseDto sweetResponseDto = inventoryService.restock(id, restockDto);
        return responseHandler.okResponse(sweetResponseDto, HttpStatus.OK, Constants.SWEET_RESTOCKED_SUCCESSFULLY);
    }
}

