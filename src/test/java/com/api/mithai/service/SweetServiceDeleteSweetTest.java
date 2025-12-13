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

import java.util.Optional;

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
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
            doNothing().when(sweetRepository).delete(existingSweet);

            // When
            assertDoesNotThrow(() -> {
                sweetService.delete(1L);
            });

            // Then
            verify(sweetRepository, times(1)).findById(1L);
            verify(sweetRepository, times(1)).delete(existingSweet);
        }

        @Test
        @DisplayName("Should delete sweet successfully with different ID")
        void shouldDeleteSweetSuccessfullyWithDifferentId() {
            // Given
            Sweet anotherSweet = new Sweet(2L, "Rasgulla", testCategory, 200.0, 75);
            when(sweetRepository.findById(2L)).thenReturn(Optional.of(anotherSweet));
            doNothing().when(sweetRepository).delete(anotherSweet);

            // When
            assertDoesNotThrow(() -> {
                sweetService.delete(2L);
            });

            // Then
            verify(sweetRepository, times(1)).findById(2L);
            verify(sweetRepository, times(1)).delete(anotherSweet);
        }
    }

    @Nested
    @DisplayName("Delete Sweet ID Validation Tests")
    class DeleteSweetIdValidationTests {

        @Test
        @DisplayName("Should throw ResponseStatusException when sweet not found")
        void shouldThrowExceptionWhenSweetNotFound() {
            // Given
            when(sweetRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                sweetService.delete(999L);
            });
            assertEquals("Sweet not found", exception.getMessage());
            verify(sweetRepository, times(1)).findById(999L);
            verify(sweetRepository, never()).delete(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw ResponseStatusException when sweet ID is null")
        void shouldThrowExceptionWhenSweetIdIsNull() {
            // Given
            when(sweetRepository.findById(null)).thenReturn(Optional.empty());

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                sweetService.delete(null);
            });
            assertEquals("Sweet not found", exception.getMessage());
            verify(sweetRepository, times(1)).findById(null);
            verify(sweetRepository, never()).delete(any(Sweet.class));
        }
    }

    @Nested
    @DisplayName("Delete Sweet Integration Tests")
    class DeleteSweetIntegrationTests {

        @Test
        @DisplayName("Should verify correct sweet is deleted")
        void shouldVerifyCorrectSweetIsDeleted() {
            // Given
            Sweet sweetToDelete = new Sweet(1L, "Gulab Jamun", testCategory, 150.0, 50);
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(sweetToDelete));
            doNothing().when(sweetRepository).delete(sweetToDelete);

            // When
            sweetService.delete(1L);

            // Then
            verify(sweetRepository, times(1)).findById(1L);
            verify(sweetRepository, times(1)).delete(argThat(sweet ->
                    sweet.getId().equals(1L) &&
                    sweet.getName().equals("Gulab Jamun")
            ));
            verify(sweetRepository, never()).delete(any(Sweet.class));
        }

        @Test
        @DisplayName("Should not delete when sweet not found")
        void shouldNotDeleteWhenSweetNotFound() {
            // Given
            when(sweetRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(ResponseStatusException.class, () -> {
                sweetService.delete(999L);
            });

            verify(sweetRepository, times(1)).findById(999L);
            verify(sweetRepository, never()).delete(any(Sweet.class));
        }

        @Test
        @DisplayName("Should only call delete once for valid sweet")
        void shouldOnlyCallDeleteOnceForValidSweet() {
            // Given
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
            doNothing().when(sweetRepository).delete(existingSweet);

            // When
            sweetService.delete(1L);

            // Then
            verify(sweetRepository, times(1)).findById(1L);
            verify(sweetRepository, times(1)).delete(existingSweet);
            verifyNoMoreInteractions(sweetRepository);
        }
    }
}

