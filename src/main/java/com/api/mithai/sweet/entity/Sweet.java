package com.api.mithai.sweet.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sweets")
@Getter
@Setter
@NoArgsConstructor
public class Sweet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Sweet name cannot be null or empty")
    @Pattern(regexp = "^\\S.*\\S$|^\\S+$", message = "Sweet name cannot be blank")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Category ID cannot be null")
    private Long categoryId;

    @NotNull(message = "Price cannot be null")
    @Min(value = 1, message = "Price must be greater than zero")
    @Column(nullable = false)
    private Double price;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(nullable = false)
    private Integer quantity;

    public Sweet(Long id, String name, Long categoryId, Double price, Integer quantity) {
        // Validate name
        if (name == null) {
            throw new IllegalArgumentException("Sweet name cannot be null or empty");
        }
        if (name.isEmpty() || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Sweet name cannot be null or empty");
        }

        // Validate categoryId
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }

        // Validate price
        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }

        // Validate quantity
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
        this.quantity = quantity;
    }
}
