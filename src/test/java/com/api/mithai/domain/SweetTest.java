package com.api.mithai.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Domain tests for Sweet entity following TDD approach
 */
@DisplayName("Sweet Domain Tests")
class SweetTest {

    @Nested
    @DisplayName("Sweet Creation Tests")
    class SweetCreationTests {

        @Test
        @DisplayName("Should create sweet with valid data")
        void shouldCreateSweetWithValidData() {
            // Given
            Long id = 1L;
            String name = "Gulab Jamun";
            Long categoryId = 1L;
            Double price = 150.0;
            Integer quantity = 50;

            // When
            Sweet sweet = new Sweet(id, name, categoryId, price, quantity);

            // Then
            assertNotNull(sweet);
            assertEquals(id, sweet.getId());
            assertEquals(name, sweet.getName());
            assertEquals(categoryId, sweet.getCategoryId());
            assertEquals(price, sweet.getPrice());
            assertEquals(quantity, sweet.getQuantity());
        }

        @Test
        @DisplayName("Should throw exception when name is null")
        void shouldThrowExceptionWhenNameIsNull() {
            // Given
            Long id = 1L;
            String name = null;
            Long categoryId = 1L;
            Double price = 150.0;
            Integer quantity = 50;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new Sweet(id, name, categoryId, price, quantity);
            }, "Sweet name cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw exception when name is empty")
        void shouldThrowExceptionWhenNameIsEmpty() {
            // Given
            Long id = 1L;
            String name = "";
            Long categoryId = 1L;
            Double price = 150.0;
            Integer quantity = 50;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new Sweet(id, name, categoryId, price, quantity);
            }, "Sweet name cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw exception when name is blank")
        void shouldThrowExceptionWhenNameIsBlank() {
            // Given
            Long id = 1L;
            String name = "   ";
            Long categoryId = 1L;
            Double price = 150.0;
            Integer quantity = 50;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new Sweet(id, name, categoryId, price, quantity);
            }, "Sweet name cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw exception when categoryId is null")
        void shouldThrowExceptionWhenCategoryIdIsNull() {
            // Given
            Long id = 1L;
            String name = "Gulab Jamun";
            Long categoryId = null;
            Double price = 150.0;
            Integer quantity = 50;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new Sweet(id, name, categoryId, price, quantity);
            }, "Category ID cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when price is null")
        void shouldThrowExceptionWhenPriceIsNull() {
            // Given
            Long id = 1L;
            String name = "Gulab Jamun";
            Long categoryId = 1L;
            Double price = null;
            Integer quantity = 50;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new Sweet(id, name, categoryId, price, quantity);
            }, "Price cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when price is zero")
        void shouldThrowExceptionWhenPriceIsZero() {
            // Given
            Long id = 1L;
            String name = "Gulab Jamun";
            Long categoryId = 1L;
            Double price = 0.0;
            Integer quantity = 50;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new Sweet(id, name, categoryId, price, quantity);
            }, "Price must be greater than zero");
        }

        @Test
        @DisplayName("Should throw exception when price is negative")
        void shouldThrowExceptionWhenPriceIsNegative() {
            // Given
            Long id = 1L;
            String name = "Gulab Jamun";
            Long categoryId = 1L;
            Double price = -10.0;
            Integer quantity = 50;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new Sweet(id, name, categoryId, price, quantity);
            }, "Price must be greater than zero");
        }

        @Test
        @DisplayName("Should throw exception when quantity is null")
        void shouldThrowExceptionWhenQuantityIsNull() {
            // Given
            Long id = 1L;
            String name = "Gulab Jamun";
            Long categoryId = 1L;
            Double price = 150.0;
            Integer quantity = null;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new Sweet(id, name, categoryId, price, quantity);
            }, "Quantity cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when quantity is negative")
        void shouldThrowExceptionWhenQuantityIsNegative() {
            // Given
            Long id = 1L;
            String name = "Gulab Jamun";
            Long categoryId = 1L;
            Double price = 150.0;
            Integer quantity = -10;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new Sweet(id, name, categoryId, price, quantity);
            }, "Quantity cannot be negative");
        }

        @Test
        @DisplayName("Should accept zero quantity")
        void shouldAcceptZeroQuantity() {
            // Given
            Long id = 1L;
            String name = "Gulab Jamun";
            Long categoryId = 1L;
            Double price = 150.0;
            Integer quantity = 0;

            // When & Then
            assertDoesNotThrow(() -> {
                new Sweet(id, name, categoryId, price, quantity);
            }, "Zero quantity should be allowed (out of stock)");
        }
    }
}

