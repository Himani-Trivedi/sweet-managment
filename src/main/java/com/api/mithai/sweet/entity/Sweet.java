package com.api.mithai.sweet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sweets")
@Getter
@NoArgsConstructor
public class Sweet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private SweetCategory category;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer quantity;

    public Sweet(Long id, String name, SweetCategory category, Double price, Integer quantity) {
        // Validate name
        if (name == null) {
            throw new IllegalArgumentException("Sweet name cannot be null or empty");
        }
        if (name.isEmpty() || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Sweet name cannot be null or empty");
        }

        if (category == null) {
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
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }

    // Custom setters with validation
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        // Validate name
        if (name == null) {
            throw new IllegalArgumentException("Sweet name cannot be null or empty");
        }
        if (name.isEmpty() || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Sweet name cannot be null or empty");
        }
        this.name = name;
    }

    public void setCategory(SweetCategory category) {
        // Validate category
        if (category == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }
        this.category = category;
    }

    public void setPrice(Double price) {
        // Validate price
        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        this.price = price;
    }

    public void setQuantity(Integer quantity) {
        // Validate quantity
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }
}
