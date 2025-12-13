package com.api.mithai.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Domain tests for SweetCategory entity following TDD approach
 */
@DisplayName("SweetCategory Domain Tests")
class SweetCategoryTest {

    @Nested
    @DisplayName("SweetCategory Creation Tests")
    class SweetCategoryCreationTests {

        @Test
        @DisplayName("Should create sweet category with valid data")
        void shouldCreateSweetCategoryWithValidData() {
            // Given
            Long id = 1L;
            String name = "Milk Sweets";

            // When
            SweetCategory category = new SweetCategory(id, name);

            // Then
            assertNotNull(category);
            assertEquals(id, category.getId());
            assertEquals(name, category.getName());
        }

        @Test
        @DisplayName("Should throw exception when name is null")
        void shouldThrowExceptionWhenNameIsNull() {
            // Given
            Long id = 1L;
            String name = null;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new SweetCategory(id, name);
            }, "Category name cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw exception when name is empty")
        void shouldThrowExceptionWhenNameIsEmpty() {
            // Given
            Long id = 1L;
            String name = "";

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new SweetCategory(id, name);
            }, "Category name cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw exception when name is blank")
        void shouldThrowExceptionWhenNameIsBlank() {
            // Given
            Long id = 1L;
            String name = "   ";

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                new SweetCategory(id, name);
            }, "Category name cannot be null or empty");
        }

        @Test
        @DisplayName("Should accept null ID for new entities")
        void shouldThrowExceptionWhenNameIsBlank() {
            // Given
            Long id = null;
            String name = "Milk Sweets";

            // When & Then
            assertDoesNotThrow(() -> {
                new SweetCategory(id, name);
            }, "Null ID should be allowed for new entities");
        }

        @Test
        @DisplayName("Should trim whitespace from name")
        void shouldTrimWhitespaceFromName() {
            // Given
            Long id = 1L;
            String nameWithSpaces = "  Milk Sweets  ";

            // When
            SweetCategory category = new SweetCategory(id, nameWithSpaces);

            // Then
            assertEquals("Milk Sweets", category.getName());
        }
    }
}

