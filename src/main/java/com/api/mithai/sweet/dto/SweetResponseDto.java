package com.api.mithai.sweet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SweetResponseDto {
    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private Double price;
    private Integer quantity;
}

