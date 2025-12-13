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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Urls.BASE_URL + Urls.SWEETS_URL)
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final ResponseHandler responseHandler;

    @PostMapping("/{id}" + Urls.PURCHASE_URL)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseResponse> purchase(
            @PathVariable Long id,
            @RequestBody @Valid PurchaseDto purchaseDto) {
        SweetResponseDto sweetResponseDto = inventoryService.purchase(id, purchaseDto);
        return responseHandler.okResponse(sweetResponseDto, HttpStatus.OK, Constants.SWEET_PURCHASED_SUCCESSFULLY);
    }

    @PostMapping("/{id}" + Urls.RESTOCK_URL)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse> restock(
            @PathVariable Long id,
            @RequestBody @Valid RestockDto restockDto) {
        SweetResponseDto sweetResponseDto = inventoryService.restock(id, restockDto);
        return responseHandler.okResponse(sweetResponseDto, HttpStatus.OK, Constants.SWEET_RESTOCKED_SUCCESSFULLY);
    }
}

