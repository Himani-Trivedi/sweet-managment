package com.api.mithai.service;

import com.api.mithai.base.exception.ResponseStatusException;
import com.api.mithai.sweet.entity.Sweet;
import com.api.mithai.sweet.entity.SweetCategory;
import com.api.mithai.sweet.repository.SweetCategoryRepository;
import com.api.mithai.sweet.repository.SweetRepository;
import com.api.mithai.sweet.service.SweetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SweetService Delete Sweet Tests - TDD Red Phase")
public class SweetServiceDeleteSweetTest {

    @Mock
    private SweetRepository sweetRepository;

    @Mock
    private SweetCategoryRepository sweetCategoryRepository;

    @InjectMocks
    private SweetService sweetService;

    private Sweet existingSweet;
    private SweetCategory testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new SweetCategory(1L, "Milk Sweets");
        existingSweet = new Sweet(1L, "Gulab Jamun", testCategory, 150.0, 50);
    }

    @Nested
    @DisplayName("Delete Sweet Success Tests")
    class DeleteSweetSuccessTests {

        @Test
        @DisplayName("Should delete sweet successfully when sweet exists")
        void shouldDeleteSweetSuccessfullyWhenSweetExists() {
            // Given
            when(sweetRepository.existsById(1L)).thenReturn(true);
            doNothing().when(sweetRepository).deleteById(1L);

            // When
            assertDoesNotThrow(() -> {
                sweetService.delete(1L);
            });

            // Then
            verify(sweetRepository, times(1)).existsById(1L);
            verify(sweetRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should delete sweet successfully with different ID")
        void shouldDeleteSweetSuccessfullyWithDifferentId() {
            // Given
            when(sweetRepository.existsById(2L)).thenReturn(true);
            doNothing().when(sweetRepository).deleteById(2L);

            // When
            assertDoesNotThrow(() -> {
                sweetService.delete(2L);
            });

            // Then
            verify(sweetRepository, times(1)).existsById(2L);
            verify(sweetRepository, times(1)).deleteById(2L);
        }
    }

    @Nested
    @DisplayName("Delete Sweet ID Validation Tests")
    class DeleteSweetIdValidationTests {

        @Test
        @DisplayName("Should throw ResponseStatusException when sweet not found")
        void shouldThrowExceptionWhenSweetNotFound() {
            // Given
            when(sweetRepository.existsById(999L)).thenReturn(false);

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                sweetService.delete(999L);
            });
            assertEquals("Sweet not found", exception.getMessage());
            verify(sweetRepository, times(1)).existsById(999L);
            verify(sweetRepository, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("Should throw ResponseStatusException when sweet ID is null")
        void shouldThrowExceptionWhenSweetIdIsNull() {
            // Given
            when(sweetRepository.existsById(null)).thenReturn(false);

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                sweetService.delete(null);
            });
            assertEquals("Sweet not found", exception.getMessage());
            verify(sweetRepository, times(1)).existsById(null);
            verify(sweetRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("Delete Sweet Integration Tests")
    class DeleteSweetIntegrationTests {

        @Test
        @DisplayName("Should verify correct sweet is deleted")
        void shouldVerifyCorrectSweetIsDeleted() {
            // Given
            when(sweetRepository.existsById(1L)).thenReturn(true);
            doNothing().when(sweetRepository).deleteById(1L);

            // When
            sweetService.delete(1L);

            // Then
            verify(sweetRepository, times(1)).existsById(1L);
            verify(sweetRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should not delete when sweet not found")
        void shouldNotDeleteWhenSweetNotFound() {
            // Given
            when(sweetRepository.existsById(999L)).thenReturn(false);

            // When & Then
            assertThrows(ResponseStatusException.class, () -> {
                sweetService.delete(999L);
            });

            verify(sweetRepository, times(1)).existsById(999L);
            verify(sweetRepository, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("Should only call delete once for valid sweet")
        void shouldOnlyCallDeleteOnceForValidSweet() {
            // Given
            when(sweetRepository.existsById(1L)).thenReturn(true);
            doNothing().when(sweetRepository).deleteById(1L);

            // When
            sweetService.delete(1L);

            // Then
            verify(sweetRepository, times(1)).existsById(1L);
            verify(sweetRepository, times(1)).deleteById(1L);
            verifyNoMoreInteractions(sweetRepository);
        }
    }
}

