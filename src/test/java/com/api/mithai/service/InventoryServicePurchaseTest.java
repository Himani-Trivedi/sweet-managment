package com.api.mithai.service;

import com.api.mithai.base.exception.ResponseStatusException;
import com.api.mithai.sweet.dto.PurchaseDto;
import com.api.mithai.sweet.dto.SweetResponseDto;
import com.api.mithai.sweet.entity.Sweet;
import com.api.mithai.sweet.entity.SweetCategory;
import com.api.mithai.sweet.repository.SweetRepository;
import com.api.mithai.sweet.service.InventoryService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryService Purchase Tests - TDD Red Phase")
public class InventoryServicePurchaseTest {

    @Mock
    private SweetRepository sweetRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private PurchaseDto purchaseDto;
    private Sweet existingSweet;
    private SweetCategory testCategory;

    @BeforeEach
    void setUp() {
        purchaseDto = new PurchaseDto();
        purchaseDto.setPurchaseQuantity(10);

        testCategory = new SweetCategory(1L, "Milk Sweets");
        existingSweet = new Sweet(1L, "Gulab Jamun", testCategory, 150.0, 50);
    }

    @Nested
    @DisplayName("Purchase Sweet Success Tests")
    class PurchaseSweetSuccessTests {

        @Test
        @DisplayName("Should purchase sweet successfully when quantity is available")
        void shouldPurchaseSweetSuccessfullyWhenQuantityIsAvailable() {
            // Given
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
            when(sweetRepository.save(any(Sweet.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            SweetResponseDto result = inventoryService.purchase(1L, purchaseDto);

            // Then
            assertNotNull(result);
            assertEquals(40, result.getQuantity()); // 50 - 10 = 40
            verify(sweetRepository, times(1)).findById(1L);
            verify(sweetRepository, times(1)).save(argThat(sweet ->
                    sweet.getId().equals(1L) &&
                    sweet.getQuantity().equals(40)
            ));
        }

        @Test
        @DisplayName("Should purchase sweet successfully when purchase quantity equals current quantity")
        void shouldPurchaseSweetSuccessfullyWhenPurchaseQuantityEqualsCurrentQuantity() {
            // Given
            purchaseDto.setPurchaseQuantity(50); // Equal to current quantity
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
            when(sweetRepository.save(any(Sweet.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            SweetResponseDto result = inventoryService.purchase(1L, purchaseDto);

            // Then
            assertNotNull(result);
            assertEquals(0, result.getQuantity()); // 50 - 50 = 0
            verify(sweetRepository, times(1)).save(any(Sweet.class));
        }
    }

    @Nested
    @DisplayName("Purchase Sweet ID Validation Tests")
    class PurchaseSweetIdValidationTests {

        @Test
        @DisplayName("Should throw ResponseStatusException when sweet not found")
        void shouldThrowExceptionWhenSweetNotFound() {
            // Given
            when(sweetRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                inventoryService.purchase(999L, purchaseDto);
            });
            assertEquals("Sweet not found", exception.getMessage());
            verify(sweetRepository, times(1)).findById(999L);
            verify(sweetRepository, never()).save(any(Sweet.class));
        }
    }

    @Nested
    @DisplayName("Purchase Sweet Quantity Validation Tests")
    class PurchaseSweetQuantityValidationTests {

        @Test
        @DisplayName("Should throw ResponseStatusException when purchase quantity is null")
        void shouldThrowExceptionWhenPurchaseQuantityIsNull() {
            // Given
            purchaseDto.setPurchaseQuantity(null);
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                inventoryService.purchase(1L, purchaseDto);
            });
            assertEquals("Purchase quantity cannot be null", exception.getMessage());
            verify(sweetRepository, times(1)).findById(1L);
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw ResponseStatusException when purchase quantity is zero")
        void shouldThrowExceptionWhenPurchaseQuantityIsZero() {
            // Given
            purchaseDto.setPurchaseQuantity(0);
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                inventoryService.purchase(1L, purchaseDto);
            });
            assertEquals("Purchase quantity must be greater than zero", exception.getMessage());
            verify(sweetRepository, times(1)).findById(1L);
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw ResponseStatusException when purchase quantity is negative")
        void shouldThrowExceptionWhenPurchaseQuantityIsNegative() {
            // Given
            purchaseDto.setPurchaseQuantity(-5);
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                inventoryService.purchase(1L, purchaseDto);
            });
            assertEquals("Purchase quantity must be greater than zero", exception.getMessage());
            verify(sweetRepository, times(1)).findById(1L);
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw ResponseStatusException when purchase quantity exceeds current quantity")
        void shouldThrowExceptionWhenPurchaseQuantityExceedsCurrentQuantity() {
            // Given
            purchaseDto.setPurchaseQuantity(60); // More than current quantity (50)
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                inventoryService.purchase(1L, purchaseDto);
            });
            assertEquals("Purchase quantity cannot exceed available quantity", exception.getMessage());
            verify(sweetRepository, times(1)).findById(1L);
            verify(sweetRepository, never()).save(any(Sweet.class));
        }
    }

    @Nested
    @DisplayName("Purchase Sweet Integration Tests")
    class PurchaseSweetIntegrationTests {

        @Test
        @DisplayName("Should reduce quantity correctly after purchase")
        void shouldReduceQuantityCorrectlyAfterPurchase() {
            // Given
            purchaseDto.setPurchaseQuantity(25);
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
            when(sweetRepository.save(any(Sweet.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            SweetResponseDto result = inventoryService.purchase(1L, purchaseDto);

            // Then
            assertEquals(25, result.getQuantity()); // 50 - 25 = 25
            verify(sweetRepository, times(1)).save(argThat(sweet ->
                    sweet.getQuantity().equals(25) &&
                    sweet.getId().equals(1L) &&
                    sweet.getName().equals("Gulab Jamun")
            ));
        }

        @Test
        @DisplayName("Should not save when validation fails")
        void shouldNotSaveWhenValidationFails() {
            // Given
            purchaseDto.setPurchaseQuantity(100); // Exceeds available quantity
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));

            // When & Then
            assertThrows(ResponseStatusException.class, () -> {
                inventoryService.purchase(1L, purchaseDto);
            });

            verify(sweetRepository, times(1)).findById(1L);
            verify(sweetRepository, never()).save(any(Sweet.class));
        }
    }
}

