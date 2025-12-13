package com.api.mithai.sweet.service;

import com.api.mithai.base.constants.Constants;
import com.api.mithai.base.exception.ResponseStatusException;
import com.api.mithai.sweet.dto.SweetRequestDto;
import com.api.mithai.sweet.dto.SweetResponseDto;
import com.api.mithai.sweet.entity.Sweet;
import com.api.mithai.sweet.entity.SweetCategory;
import com.api.mithai.sweet.repository.SweetCategoryRepository;
import com.api.mithai.sweet.repository.SweetRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SweetService {

    private final SweetRepository sweetRepository;
    private final SweetCategoryRepository sweetCategoryRepository;

    public SweetResponseDto create(SweetRequestDto sweetRequestDto) {
        // Validate all input fields first
        String name = validateName(sweetRequestDto.getName());
        Long categoryId = validateCategoryId(sweetRequestDto.getCategoryId());
        Double price = validatePrice(sweetRequestDto.getPrice());
        Integer quantity = validateQuantity(sweetRequestDto.getQuantity());
        
        // Check for duplicate name (case-insensitive)
        if (sweetRepository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(Constants.SWEET_NAME_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }

        // Get category after all validations pass
        SweetCategory category = sweetCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException("Category not found", HttpStatus.BAD_REQUEST));

        // Create and save sweet
        Sweet sweet = new Sweet();
        sweet.setName(name);
        sweet.setCategory(category);
        sweet.setPrice(price);
        sweet.setQuantity(quantity);
        
        Sweet savedSweet = sweetRepository.save(sweet);
        
        return mapToResponseDto(savedSweet);
    }

    public SweetResponseDto update(Long id, SweetRequestDto sweetRequestDto) {
        // Check if sweet exists
        Sweet existingSweet = sweetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException("Sweet not found", HttpStatus.BAD_REQUEST));

        // Validate all input fields first
        String name = validateName(sweetRequestDto.getName());
        Long categoryId = validateCategoryId(sweetRequestDto.getCategoryId());
        Double price = validatePrice(sweetRequestDto.getPrice());
        Integer quantity = validateQuantity(sweetRequestDto.getQuantity());
        
        // Check for duplicate name (case-insensitive) excluding current sweet
        if (!name.equalsIgnoreCase(existingSweet.getName()) && 
            sweetRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new ResponseStatusException(Constants.SWEET_NAME_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }

        SweetCategory category = sweetCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException("Category not found", HttpStatus.BAD_REQUEST));

        // Update sweet
        existingSweet.setName(name);
        existingSweet.setCategory(category);
        existingSweet.setPrice(price);
        existingSweet.setQuantity(quantity);
        
        Sweet updatedSweet = sweetRepository.save(existingSweet);
        
        return mapToResponseDto(updatedSweet);
    }

    @Transactional
    public void delete(Long id) {
        Sweet sweet = sweetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException("Sweet not found", HttpStatus.BAD_REQUEST));
        
        sweetRepository.delete(sweet);
    }

    public static String validateName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Sweet name cannot be null or empty");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Sweet name cannot be null or empty");
        }
        String trimmedName = name.trim();
        if (trimmedName.isEmpty()) {
            throw new IllegalArgumentException("Sweet name cannot be null or empty");
        }
        return trimmedName;
    }

    public static Long validateCategoryId(Long categoryId) {
        if (categoryId == null) {
            throw new ResponseStatusException("Category ID cannot be null", HttpStatus.BAD_REQUEST);
        }
        return categoryId;
    }

    public static Double validatePrice(Double price) {
        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        return price;
    }

    public static Integer validateQuantity(Integer quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        return quantity;
    }

    private SweetResponseDto mapToResponseDto(Sweet sweet) {
        return new SweetResponseDto(
                sweet.getId(),
                sweet.getName(),
                sweet.getCategory().getId(),
                sweet.getCategory().getName(),
                sweet.getPrice(),
                sweet.getQuantity()
        );
    }
}
