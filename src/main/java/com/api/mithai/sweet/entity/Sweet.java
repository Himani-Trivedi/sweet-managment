package com.api.mithai.sweet.entity;

import com.api.mithai.sweet.service.SweetService;
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
        this.id = id;
        this.name = SweetService.validateName(name);
        
        if (category == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }
        this.category = category;
        
        this.price = SweetService.validatePrice(price);
        this.quantity = SweetService.validateQuantity(quantity);
    }

    // Custom setters with validation
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = SweetService.validateName(name);
    }

    public void setCategory(SweetCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }
        this.category = category;
    }

    public void setPrice(Double price) {
        this.price = SweetService.validatePrice(price);
    }

    public void setQuantity(Integer quantity) {
        this.quantity = SweetService.validateQuantity(quantity);
    }
}
