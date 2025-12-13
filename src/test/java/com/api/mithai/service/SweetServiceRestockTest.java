package com.api.mithai.service;

import com.api.mithai.base.exception.ResponseStatusException;
import com.api.mithai.sweet.dto.RestockDto;
import com.api.mithai.sweet.dto.SweetResponseDto;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SweetService Restock Tests - TDD Red Phase")
public class SweetServiceRestockTest {

    @Mock
    private SweetRepository sweetRepository;

    @Mock
    private SweetCategoryRepository sweetCategoryRepository;

    @InjectMocks
    private SweetService sweetService;

    private RestockDto restockDto;
    private Sweet existingSweet;
    private SweetCategory testCategory;

    @BeforeEach
    void setUp() {
        restockDto = new RestockDto();
        restockDto.setQuantity(20);

        testCategory = new SweetCategory(1L, "Milk Sweets");
        existingSweet = new Sweet(1L, "Gulab Jamun", testCategory, 150.0, 50);
    }

    @Nested
    @DisplayName("Restock Sweet Success Tests")
    class RestockSweetSuccessTests {

        @Test
        @DisplayName("Should restock sweet successfully with valid quantity")
        void shouldRestockSweetSuccessfullyWithValidQuantity() {
            // Given
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
            when(sweetRepository.save(any(Sweet.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            SweetResponseDto result = sweetService.restock(1L, restockDto);

            // Then
            assertNotNull(result);
            assertEquals(70, result.getQuantity()); // 50 + 20 = 70
            verify(sweetRepository, times(1)).findById(1L);
            verify(sweetRepository, times(1)).save(argThat(sweet ->
                    sweet.getId().equals(1L) &&
                    sweet.getQuantity().equals(70)
            ));
        }

        @Test
        @DisplayName("Should restock sweet successfully with large quantity")
        void shouldRestockSweetSuccessfullyWithLargeQuantity() {
            // Given
            restockDto.setQuantity(100);
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
            when(sweetRepository.save(any(Sweet.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            SweetResponseDto result = sweetService.restock(1L, restockDto);

            // Then
            assertNotNull(result);
            assertEquals(150, result.getQuantity()); // 50 + 100 = 150
            verify(sweetRepository, times(1)).save(any(Sweet.class));
        }
    }

    @Nested
    @DisplayName("Restock Sweet ID Validation Tests")
    class RestockSweetIdValidationTests {

        @Test
        @DisplayName("Should throw ResponseStatusException when sweet not found")
        void shouldThrowExceptionWhenSweetNotFound() {
            // Given
            when(sweetRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                sweetService.restock(999L, restockDto);
            });
            assertEquals("Sweet not found", exception.getMessage());
            verify(sweetRepository, times(1)).findById(999L);
            verify(sweetRepository, never()).save(any(Sweet.class));
        }
    }

    @Nested
    @DisplayName("Restock Sweet Quantity Validation Tests")
    class RestockSweetQuantityValidationTests {

        @Test
        @DisplayName("Should throw ResponseStatusException when quantity is null")
        void shouldThrowExceptionWhenQuantityIsNull() {
            // Given
            restockDto.setQuantity(null);
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                sweetService.restock(1L, restockDto);
            });
            assertEquals("Restock quantity cannot be null", exception.getMessage());
            verify(sweetRepository, times(1)).findById(1L);
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw ResponseStatusException when quantity is zero")
        void shouldThrowExceptionWhenQuantityIsZero() {
            // Given
            restockDto.setQuantity(0);
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                sweetService.restock(1L, restockDto);
            });
            assertEquals("Restock quantity must be greater than zero", exception.getMessage());
            verify(sweetRepository, times(1)).findById(1L);
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw ResponseStatusException when quantity is negative")
        void shouldThrowExceptionWhenQuantityIsNegative() {
            // Given
            restockDto.setQuantity(-10);
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                sweetService.restock(1L, restockDto);
            });
            assertEquals("Restock quantity must be greater than zero", exception.getMessage());
            verify(sweetRepository, times(1)).findById(1L);
            verify(sweetRepository, never()).save(any(Sweet.class));
        }
    }

    @Nested
    @DisplayName("Restock Sweet Integration Tests")
    class RestockSweetIntegrationTests {

        @Test
        @DisplayName("Should increase quantity correctly after restock")
        void shouldIncreaseQuantityCorrectlyAfterRestock() {
            // Given
            restockDto.setQuantity(30);
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
            when(sweetRepository.save(any(Sweet.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            SweetResponseDto result = sweetService.restock(1L, restockDto);

            // Then
            assertEquals(80, result.getQuantity()); // 50 + 30 = 80
            verify(sweetRepository, times(1)).save(argThat(sweet ->
                    sweet.getQuantity().equals(80) &&
                    sweet.getId().equals(1L) &&
                    sweet.getName().equals("Gulab Jamun")
            ));
        }

        @Test
        @DisplayName("Should not save when validation fails")
        void shouldNotSaveWhenValidationFails() {
            // Given
            restockDto.setQuantity(0);
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));

            // When & Then
            assertThrows(ResponseStatusException.class, () -> {
                sweetService.restock(1L, restockDto);
            });

            verify(sweetRepository, times(1)).findById(1L);
            verify(sweetRepository, never()).save(any(Sweet.class));
        }
    }
}

