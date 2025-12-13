package com.api.mithai.service;

import com.api.mithai.base.constants.Constants;
import com.api.mithai.base.exception.ResponseStatusException;
import com.api.mithai.sweet.dto.SweetRequestDto;
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
@DisplayName("SweetService Update Sweet Tests - TDD Red Phase")
public class SweetServiceUpdateSweetTest {

    @Mock
    private SweetRepository sweetRepository;

    @Mock
    private SweetCategoryRepository sweetCategoryRepository;

    @InjectMocks
    private SweetService sweetService;

    private SweetRequestDto updateSweetRequest;
    private Sweet existingSweet;
    private SweetCategory testCategory;
    private SweetCategory newCategory;

    @BeforeEach
    void setUp() {
        updateSweetRequest = new SweetRequestDto();
        updateSweetRequest.setName("Rasgulla");
        updateSweetRequest.setCategoryId(2L);
        updateSweetRequest.setPrice(200.0);
        updateSweetRequest.setQuantity(75);

        testCategory = new SweetCategory(1L, "Milk Sweets");
        newCategory = new SweetCategory(2L, "Traditional Sweets");
        
        existingSweet = new Sweet(1L, "Gulab Jamun", testCategory, 150.0, 50);
    }

    @Nested
    @DisplayName("Update Sweet Success Tests")
    class UpdateSweetSuccessTests {

        @Test
        @DisplayName("Should update sweet successfully with valid data")
        void shouldUpdateSweetSuccessfullyWithValidData() {
            // Given
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
            when(sweetRepository.existsByNameIgnoreCaseAndIdNot("Rasgulla", 1L)).thenReturn(false);
            when(sweetCategoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
            when(sweetRepository.save(any(Sweet.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            SweetResponseDto result = sweetService.update(1L, updateSweetRequest);

            // Then
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Rasgulla", result.getName());
            assertEquals(2L, result.getCategoryId());
            assertEquals("Traditional Sweets", result.getCategoryName());
            assertEquals(200.0, result.getPrice());
            assertEquals(75, result.getQuantity());

            verify(sweetRepository, times(1)).findById(1L);
            verify(sweetRepository, times(1)).existsByNameIgnoreCaseAndIdNot("Rasgulla", 1L);
            verify(sweetCategoryRepository, times(1)).findById(2L);
            verify(sweetRepository, times(1)).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should update sweet with same name (no duplicate check needed)")
        void shouldUpdateSweetWithSameName() {
            // Given
            updateSweetRequest.setName("Gulab Jamun"); // Same name as existing
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
            when(sweetCategoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
            when(sweetRepository.save(any(Sweet.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            SweetResponseDto result = sweetService.update(1L, updateSweetRequest);

            // Then
            assertNotNull(result);
            assertEquals("Gulab Jamun", result.getName());
            verify(sweetRepository, times(1)).findById(1L);
            verify(sweetRepository, never()).existsByNameIgnoreCaseAndIdNot(anyString(), anyLong());
            verify(sweetRepository, times(1)).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should update sweet with zero quantity")
        void shouldUpdateSweetWithZeroQuantity() {
            // Given
            updateSweetRequest.setQuantity(0);
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
            when(sweetRepository.existsByNameIgnoreCaseAndIdNot("Rasgulla", 1L)).thenReturn(false);
            when(sweetCategoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
            when(sweetRepository.save(any(Sweet.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            SweetResponseDto result = sweetService.update(1L, updateSweetRequest);

            // Then
            assertNotNull(result);
            assertEquals(0, result.getQuantity());
            verify(sweetRepository, times(1)).save(any(Sweet.class));
        }
    }

    @Nested
    @DisplayName("Update Sweet ID Validation Tests")
    class UpdateSweetIdValidationTests {

        @Test
        @DisplayName("Should throw ResponseStatusException when sweet not found")
        void shouldThrowExceptionWhenSweetNotFound() {
            // Given
            when(sweetRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                sweetService.update(999L, updateSweetRequest);
            });
            assertEquals("Sweet not found", exception.getMessage());
            verify(sweetRepository, times(1)).findById(999L);
            verify(sweetRepository, never()).existsByNameIgnoreCaseAndIdNot(anyString(), anyLong());
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }
    }

    @Nested
    @DisplayName("Update Sweet Name Validation Tests")
    class UpdateSweetNameValidationTests {

        @Test
        @DisplayName("Should throw exception when name is null")
        void shouldThrowExceptionWhenNameIsNull() {
            // Given
            updateSweetRequest.setName(null);
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                sweetService.update(1L, updateSweetRequest);
            });
            assertEquals("Sweet name cannot be null or empty", exception.getMessage());
            verify(sweetRepository, times(1)).findById(1L);
            verify(sweetRepository, never()).existsByNameIgnoreCaseAndIdNot(anyString(), anyLong());
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw exception when name is empty")
        void shouldThrowExceptionWhenNameIsEmpty() {
            // Given
            updateSweetRequest.setName("");
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                sweetService.update(1L, updateSweetRequest);
            });
            assertEquals("Sweet name cannot be null or empty", exception.getMessage());
            verify(sweetRepository, times(1)).findById(1L);
            verify(sweetRepository, never()).existsByNameIgnoreCaseAndIdNot(anyString(), anyLong());
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw exception when name is blank")
        void shouldThrowExceptionWhenNameIsBlank() {
            // Given
            updateSweetRequest.setName("   ");
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                sweetService.update(1L, updateSweetRequest);
            });
            assertEquals("Sweet name cannot be null or empty", exception.getMessage());
            verify(sweetRepository, times(1)).findById(1L);
            verify(sweetRepository, never()).existsByNameIgnoreCaseAndIdNot(anyString(), anyLong());
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw ResponseStatusException when sweet name already exists (exact case)")
        void shouldThrowExceptionWhenSweetNameAlreadyExistsExactCase() {
            // Given
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
            when(sweetRepository.existsByNameIgnoreCaseAndIdNot("Rasgulla", 1L)).thenReturn(true);

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                sweetService.update(1L, updateSweetRequest);
            });
            assertEquals(Constants.SWEET_NAME_ALREADY_EXISTS, exception.getMessage());
            verify(sweetRepository, times(1)).findById(1L);
            verify(sweetRepository, times(1)).existsByNameIgnoreCaseAndIdNot("Rasgulla", 1L);
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw ResponseStatusException when sweet name already exists (case-insensitive)")
        void shouldThrowExceptionWhenSweetNameAlreadyExistsCaseInsensitive() {
            // Given
            updateSweetRequest.setName("RASGULLA"); // Different case
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
            when(sweetRepository.existsByNameIgnoreCaseAndIdNot("RASGULLA", 1L)).thenReturn(true);

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                sweetService.update(1L, updateSweetRequest);
            });
            assertEquals(Constants.SWEET_NAME_ALREADY_EXISTS, exception.getMessage());
            verify(sweetRepository, times(1)).existsByNameIgnoreCaseAndIdNot("RASGULLA", 1L);
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw ResponseStatusException when sweet name already exists (mixed case)")
        void shouldThrowExceptionWhenSweetNameAlreadyExistsMixedCase() {
            // Given
            updateSweetRequest.setName("RaSgUlLa"); // Mixed case
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
            when(sweetRepository.existsByNameIgnoreCaseAndIdNot("RaSgUlLa", 1L)).thenReturn(true);

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                sweetService.update(1L, updateSweetRequest);
            });
            assertEquals(Constants.SWEET_NAME_ALREADY_EXISTS, exception.getMessage());
            verify(sweetRepository, times(1)).existsByNameIgnoreCaseAndIdNot("RaSgUlLa", 1L);
            verify(sweetRepository, never()).save(any(Sweet.class));
        }
    }

    @Nested
    @DisplayName("Update Sweet Category Validation Tests")
    class UpdateSweetCategoryValidationTests {

        @Test
        @DisplayName("Should throw ResponseStatusException when categoryId is null")
        void shouldThrowExceptionWhenCategoryIdIsNull() {
            // Given
            updateSweetRequest.setCategoryId(null);
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
            when(sweetRepository.existsByNameIgnoreCaseAndIdNot("Rasgulla", 1L)).thenReturn(false);

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                sweetService.update(1L, updateSweetRequest);
            });
            assertEquals("Category ID cannot be null", exception.getMessage());
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw ResponseStatusException when category not found")
        void shouldThrowExceptionWhenCategoryNotFound() {
            // Given
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
            when(sweetRepository.existsByNameIgnoreCaseAndIdNot("Rasgulla", 1L)).thenReturn(false);
            when(sweetCategoryRepository.findById(999L)).thenReturn(Optional.empty());

            updateSweetRequest.setCategoryId(999L);

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                sweetService.update(1L, updateSweetRequest);
            });
            assertEquals("Category not found", exception.getMessage());
            verify(sweetCategoryRepository, times(1)).findById(999L);
            verify(sweetRepository, never()).save(any(Sweet.class));
        }
    }

    @Nested
    @DisplayName("Update Sweet Price Validation Tests")
    class UpdateSweetPriceValidationTests {

        @Test
        @DisplayName("Should throw exception when price is null")
        void shouldThrowExceptionWhenPriceIsNull() {
            // Given
            updateSweetRequest.setPrice(null);
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
            when(sweetRepository.existsByNameIgnoreCaseAndIdNot("Rasgulla", 1L)).thenReturn(false);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                sweetService.update(1L, updateSweetRequest);
            });
            assertEquals("Price cannot be null", exception.getMessage());
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw exception when price is zero")
        void shouldThrowExceptionWhenPriceIsZero() {
            // Given
            updateSweetRequest.setPrice(0.0);
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
            when(sweetRepository.existsByNameIgnoreCaseAndIdNot("Rasgulla", 1L)).thenReturn(false);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                sweetService.update(1L, updateSweetRequest);
            });
            assertEquals("Price must be greater than zero", exception.getMessage());
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw exception when price is negative")
        void shouldThrowExceptionWhenPriceIsNegative() {
            // Given
            updateSweetRequest.setPrice(-10.0);
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
            when(sweetRepository.existsByNameIgnoreCaseAndIdNot("Rasgulla", 1L)).thenReturn(false);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                sweetService.update(1L, updateSweetRequest);
            });
            assertEquals("Price must be greater than zero", exception.getMessage());
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }
    }

    @Nested
    @DisplayName("Update Sweet Quantity Validation Tests")
    class UpdateSweetQuantityValidationTests {

        @Test
        @DisplayName("Should throw exception when quantity is null")
        void shouldThrowExceptionWhenQuantityIsNull() {
            // Given
            updateSweetRequest.setQuantity(null);
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
            when(sweetRepository.existsByNameIgnoreCaseAndIdNot("Rasgulla", 1L)).thenReturn(false);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                sweetService.update(1L, updateSweetRequest);
            });
            assertEquals("Quantity cannot be null", exception.getMessage());
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw exception when quantity is negative")
        void shouldThrowExceptionWhenQuantityIsNegative() {
            // Given
            updateSweetRequest.setQuantity(-5);
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
            when(sweetRepository.existsByNameIgnoreCaseAndIdNot("Rasgulla", 1L)).thenReturn(false);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                sweetService.update(1L, updateSweetRequest);
            });
            assertEquals("Quantity cannot be negative", exception.getMessage());
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }
    }

    @Nested
    @DisplayName("Update Sweet Integration Tests")
    class UpdateSweetIntegrationTests {

        @Test
        @DisplayName("Should validate all fields before saving")
        void shouldValidateAllFieldsBeforeSaving() {
            // Given
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
            when(sweetRepository.existsByNameIgnoreCaseAndIdNot("Rasgulla", 1L)).thenReturn(false);
            when(sweetCategoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
            when(sweetRepository.save(any(Sweet.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            SweetResponseDto result = sweetService.update(1L, updateSweetRequest);

            // Then
            assertNotNull(result);
            assertEquals("Rasgulla", result.getName());
            assertEquals(2L, result.getCategoryId());
            assertEquals("Traditional Sweets", result.getCategoryName());
            assertEquals(200.0, result.getPrice());
            assertEquals(75, result.getQuantity());
            verify(sweetRepository, times(1)).findById(1L);
            verify(sweetRepository, times(1)).existsByNameIgnoreCaseAndIdNot("Rasgulla", 1L);
            verify(sweetCategoryRepository, times(1)).findById(2L);
            verify(sweetRepository, times(1)).save(argThat(sweet ->
                    sweet.getName().equals("Rasgulla") &&
                    sweet.getCategory().equals(newCategory) &&
                    sweet.getPrice().equals(200.0) &&
                    sweet.getQuantity().equals(75)
            ));
        }

        @Test
        @DisplayName("Should not save when validation fails")
        void shouldNotSaveWhenValidationFails() {
            // Given
            updateSweetRequest.setName(null);
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                sweetService.update(1L, updateSweetRequest);
            });

            verify(sweetRepository, times(1)).findById(1L);
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should allow updating only name while keeping other fields same")
        void shouldAllowUpdatingOnlyName() {
            // Given
            updateSweetRequest.setName("New Gulab Jamun");
            updateSweetRequest.setCategoryId(1L); // Same category
            updateSweetRequest.setPrice(150.0); // Same price
            updateSweetRequest.setQuantity(50); // Same quantity
            
            when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
            when(sweetRepository.existsByNameIgnoreCaseAndIdNot("New Gulab Jamun", 1L)).thenReturn(false);
            when(sweetCategoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
            when(sweetRepository.save(any(Sweet.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            SweetResponseDto result = sweetService.update(1L, updateSweetRequest);

            // Then
            assertNotNull(result);
            assertEquals("New Gulab Jamun", result.getName());
            assertEquals(1L, result.getCategoryId());
            assertEquals(150.0, result.getPrice());
            assertEquals(50, result.getQuantity());
            verify(sweetRepository, times(1)).save(any(Sweet.class));
        }
    }
}

