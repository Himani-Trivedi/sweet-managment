package com.api.mithai.sweet.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SweetRequestDto {
    @NotBlank
    private String name;

    @NotNull
    private Long categoryId;

    @NotNull
    @Min(value = 1, message = "Price must be greater than zero")
    private Double price;

    @NotNull
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
}

