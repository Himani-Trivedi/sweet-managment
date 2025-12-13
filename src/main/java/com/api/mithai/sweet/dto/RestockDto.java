package com.api.mithai.sweet.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RestockDto {
    @NotNull
    @Positive(message = "Restock quantity must be greater than zero")
    private Integer quantity;
}

