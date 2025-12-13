package com.api.mithai.sweet.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "sweet_categories")
@Getter
@Setter
@NoArgsConstructor
public class SweetCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Category name cannot be null or empty")
    @Pattern(regexp = "^\\S.*\\S$|^\\S+$", message = "Category name cannot be blank")
    @Column(nullable = false, unique = true)
    private String name;

    public SweetCategory(Long id, String name) {
        // Validate name
        if (name == null) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        if (name.isEmpty() || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }

        this.id = id;
        this.name = name.trim(); // Trim whitespace
    }
}
