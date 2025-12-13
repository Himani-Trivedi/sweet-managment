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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SweetService Create Sweet Tests - TDD Red Phase")
public class SweetServiceCreateSweetTest {

    @Mock
    private SweetRepository sweetRepository;

    @Mock
    private SweetCategoryRepository sweetCategoryRepository;

    @InjectMocks
    private SweetService sweetService;

    private SweetRequestDto createSweetRequest;
    private SweetCategory testCategory;

    @BeforeEach
    void setUp() {
        createSweetRequest = new SweetRequestDto();
        createSweetRequest.setName("Gulab Jamun");
        createSweetRequest.setCategoryId(1L);
        createSweetRequest.setPrice(150.0);
        createSweetRequest.setQuantity(50);

        testCategory = new SweetCategory(1L, "Milk Sweets");
    }

    @Nested
    @DisplayName("Create Sweet Success Tests")
    class CreateSweetSuccessTests {

        @Test
        @DisplayName("Should create sweet successfully with valid data")
        void shouldCreateSweetSuccessfullyWithValidData() {
            // Given
            when(sweetRepository.existsByNameIgnoreCase("Gulab Jamun")).thenReturn(false);
            when(sweetCategoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
            when(sweetRepository.save(any(Sweet.class))).thenAnswer(invocation -> {
                Sweet sweet = invocation.getArgument(0);
                sweet.setId(1L);
                return sweet;
            });

            // When
            SweetResponseDto result = sweetService.create(createSweetRequest);

            // Then
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Gulab Jamun", result.getName());
            assertEquals(1L, result.getCategoryId());
            assertEquals("Milk Sweets", result.getCategoryName());
            assertEquals(150.0, result.getPrice());
            assertEquals(50, result.getQuantity());

            verify(sweetRepository, times(1)).existsByNameIgnoreCase("Gulab Jamun");
            verify(sweetCategoryRepository, times(1)).findById(1L);
            verify(sweetRepository, times(1)).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should create sweet with zero quantity")
        void shouldCreateSweetWithZeroQuantity() {
            // Given
            createSweetRequest.setQuantity(0);
            when(sweetRepository.existsByNameIgnoreCase("Gulab Jamun")).thenReturn(false);
            when(sweetCategoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
            when(sweetRepository.save(any(Sweet.class))).thenAnswer(invocation -> {
                Sweet sweet = invocation.getArgument(0);
                sweet.setId(1L);
                return sweet;
            });

            // When
            SweetResponseDto result = sweetService.create(createSweetRequest);

            // Then
            assertNotNull(result);
            assertEquals(0, result.getQuantity());
            verify(sweetRepository, times(1)).existsByNameIgnoreCase("Gulab Jamun");
            verify(sweetRepository, times(1)).save(any(Sweet.class));
        }
    }

    @Nested
    @DisplayName("Create Sweet Name Validation Tests")
    class CreateSweetNameValidationTests {

        @Test
        @DisplayName("Should throw exception when name is null")
        void shouldThrowExceptionWhenNameIsNull() {
            // Given
            createSweetRequest.setName(null);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                sweetService.create(createSweetRequest);
            });
            assertEquals("Sweet name cannot be null or empty", exception.getMessage());
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw exception when name is empty")
        void shouldThrowExceptionWhenNameIsEmpty() {
            // Given
            createSweetRequest.setName("");

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                sweetService.create(createSweetRequest);
            });
            assertEquals("Sweet name cannot be null or empty", exception.getMessage());
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw exception when name is blank")
        void shouldThrowExceptionWhenNameIsBlank() {
            // Given
            createSweetRequest.setName("   ");

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                sweetService.create(createSweetRequest);
            });
            assertEquals("Sweet name cannot be null or empty", exception.getMessage());
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw ResponseStatusException when sweet name already exists (exact case)")
        void shouldThrowExceptionWhenSweetNameAlreadyExistsExactCase() {
            // Given
            Sweet existingSweet = new Sweet(1L, "Gulab Jamun", testCategory, 150.0, 50);
            when(sweetRepository.existsByNameIgnoreCase("Gulab Jamun")).thenReturn(true);

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                sweetService.create(createSweetRequest);
            });
            assertEquals(Constants.SWEET_NAME_ALREADY_EXISTS, exception.getMessage());
            verify(sweetRepository, times(1)).existsByNameIgnoreCase("Gulab Jamun");
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw ResponseStatusException when sweet name already exists (case-insensitive)")
        void shouldThrowExceptionWhenSweetNameAlreadyExistsCaseInsensitive() {
            // Given
            createSweetRequest.setName("GULAB JAMUN"); // Different case
            when(sweetRepository.existsByNameIgnoreCase("GULAB JAMUN")).thenReturn(true);

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                sweetService.create(createSweetRequest);
            });
            assertEquals(Constants.SWEET_NAME_ALREADY_EXISTS, exception.getMessage());
            verify(sweetRepository, times(1)).existsByNameIgnoreCase("GULAB JAMUN");
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw ResponseStatusException when sweet name already exists (mixed case)")
        void shouldThrowExceptionWhenSweetNameAlreadyExistsMixedCase() {
            // Given
            createSweetRequest.setName("GuLaB jAmUn"); // Mixed case
            when(sweetRepository.existsByNameIgnoreCase("GuLaB jAmUn")).thenReturn(true);

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                sweetService.create(createSweetRequest);
            });
            assertEquals(Constants.SWEET_NAME_ALREADY_EXISTS, exception.getMessage());
            verify(sweetRepository, times(1)).existsByNameIgnoreCase("GuLaB jAmUn");
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }
    }

    @Nested
    @DisplayName("Create Sweet Category Validation Tests")
    class CreateSweetCategoryValidationTests {

        @Test
        @DisplayName("Should throw ResponseStatusException when categoryId is null")
        void shouldThrowExceptionWhenCategoryIdIsNull() {
            // Given
            createSweetRequest.setCategoryId(null);

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                sweetService.create(createSweetRequest);
            });
            assertEquals("Category ID cannot be null", exception.getMessage());
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw ResponseStatusException when category not found")
        void shouldThrowExceptionWhenCategoryNotFound() {
            // Given
            when(sweetCategoryRepository.findById(999L)).thenReturn(Optional.empty());

            createSweetRequest.setCategoryId(999L);

            // When & Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                sweetService.create(createSweetRequest);
            });
            assertEquals("Category not found", exception.getMessage());
            verify(sweetCategoryRepository, times(1)).findById(999L);
            verify(sweetRepository, never()).save(any(Sweet.class));
        }
    }

    @Nested
    @DisplayName("Create Sweet Price Validation Tests")
    class CreateSweetPriceValidationTests {

        @Test
        @DisplayName("Should throw exception when price is null")
        void shouldThrowExceptionWhenPriceIsNull() {
            // Given
            createSweetRequest.setPrice(null);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                sweetService.create(createSweetRequest);
            });
            assertEquals("Price cannot be null", exception.getMessage());
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw exception when price is zero")
        void shouldThrowExceptionWhenPriceIsZero() {
            // Given
            createSweetRequest.setPrice(0.0);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                sweetService.create(createSweetRequest);
            });
            assertEquals("Price must be greater than zero", exception.getMessage());
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw exception when price is negative")
        void shouldThrowExceptionWhenPriceIsNegative() {
            // Given
            createSweetRequest.setPrice(-10.0);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                sweetService.create(createSweetRequest);
            });
            assertEquals("Price must be greater than zero", exception.getMessage());
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }
    }

    @Nested
    @DisplayName("Create Sweet Quantity Validation Tests")
    class CreateSweetQuantityValidationTests {

        @Test
        @DisplayName("Should throw exception when quantity is null")
        void shouldThrowExceptionWhenQuantityIsNull() {
            // Given
            createSweetRequest.setQuantity(null);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                sweetService.create(createSweetRequest);
            });
            assertEquals("Quantity cannot be null", exception.getMessage());
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }

        @Test
        @DisplayName("Should throw exception when quantity is negative")
        void shouldThrowExceptionWhenQuantityIsNegative() {
            // Given
            createSweetRequest.setQuantity(-5);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                sweetService.create(createSweetRequest);
            });
            assertEquals("Quantity cannot be negative", exception.getMessage());
            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }
    }

    @Nested
    @DisplayName("Create Sweet Integration Tests")
    class CreateSweetIntegrationTests {

        @Test
        @DisplayName("Should validate all fields before saving")
        void shouldValidateAllFieldsBeforeSaving() {
            // Given
            when(sweetRepository.existsByNameIgnoreCase("Gulab Jamun")).thenReturn(false);
            when(sweetCategoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
            when(sweetRepository.save(any(Sweet.class))).thenAnswer(invocation -> {
                Sweet sweet = invocation.getArgument(0);
                sweet.setId(1L);
                return sweet;
            });

            // When
            SweetResponseDto result = sweetService.create(createSweetRequest);

            // Then
            assertNotNull(result);
            assertEquals("Gulab Jamun", result.getName());
            assertEquals(1L, result.getCategoryId());
            assertEquals("Milk Sweets", result.getCategoryName());
            assertEquals(150.0, result.getPrice());
            assertEquals(50, result.getQuantity());
            verify(sweetRepository, times(1)).existsByNameIgnoreCase("Gulab Jamun");
            verify(sweetCategoryRepository, times(1)).findById(1L);
            verify(sweetRepository, times(1)).save(argThat(sweet ->
                    sweet.getName().equals("Gulab Jamun") &&
                    sweet.getCategory().equals(testCategory) &&
                    sweet.getPrice().equals(150.0) &&
                    sweet.getQuantity().equals(50)
            ));
        }

        @Test
        @DisplayName("Should not save when validation fails")
        void shouldNotSaveWhenValidationFails() {
            // Given
            createSweetRequest.setName(null);

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                sweetService.create(createSweetRequest);
            });

            verify(sweetCategoryRepository, never()).findById(anyLong());
            verify(sweetRepository, never()).save(any(Sweet.class));
        }
    }
}

