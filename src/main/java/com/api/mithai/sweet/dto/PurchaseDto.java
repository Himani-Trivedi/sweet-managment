package com.api.mithai.sweet.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PurchaseDto {
    @NotNull
    @Positive(message = "Purchase quantity must be greater than zero")
    private Integer purchaseQuantity;
}

