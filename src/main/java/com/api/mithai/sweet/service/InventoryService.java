package com.api.mithai.sweet.service;

import com.api.mithai.base.exception.ResponseStatusException;
import com.api.mithai.sweet.dto.PurchaseDto;
import com.api.mithai.sweet.dto.RestockDto;
import com.api.mithai.sweet.dto.SweetResponseDto;
import com.api.mithai.sweet.entity.Sweet;
import com.api.mithai.sweet.repository.SweetRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class InventoryService {

    private final SweetRepository sweetRepository;

    @Transactional
    public SweetResponseDto purchase(Long id, PurchaseDto purchaseDto) {
        // Check if sweet exists
        Sweet sweet = sweetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException("Sweet not found", HttpStatus.BAD_REQUEST));

        // Validate purchase quantity
        Integer purchaseQuantity = validatePurchaseQuantity(purchaseDto.getPurchaseQuantity());

        // Validate purchase quantity does not exceed available quantity
        if (purchaseQuantity > sweet.getQuantity()) {
            throw new ResponseStatusException("Purchase quantity cannot exceed available quantity", HttpStatus.BAD_REQUEST);
        }

        // Reduce quantity
        Integer newQuantity = sweet.getQuantity() - purchaseQuantity;
        sweet.setQuantity(newQuantity);

        // Save and return
        Sweet updatedSweet = sweetRepository.save(sweet);
        return mapToResponseDto(updatedSweet);
    }

    @Transactional
    public SweetResponseDto restock(Long id, RestockDto restockDto) {
        // Check if sweet exists
        Sweet sweet = sweetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException("Sweet not found", HttpStatus.BAD_REQUEST));

        // Validate restock quantity
        Integer restockQuantity = validateRestockQuantity(restockDto.getQuantity());

        // Increase quantity
        Integer newQuantity = sweet.getQuantity() + restockQuantity;
        sweet.setQuantity(newQuantity);

        // Save and return
        Sweet updatedSweet = sweetRepository.save(sweet);
        return mapToResponseDto(updatedSweet);
    }

    private Integer validatePurchaseQuantity(Integer purchaseQuantity) {
        if (purchaseQuantity == null) {
            throw new ResponseStatusException("Purchase quantity cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (purchaseQuantity <= 0) {
            throw new ResponseStatusException("Purchase quantity must be greater than zero", HttpStatus.BAD_REQUEST);
        }
        return purchaseQuantity;
    }

    private Integer validateRestockQuantity(Integer quantity) {
        if (quantity == null) {
            throw new ResponseStatusException("Restock quantity cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (quantity <= 0) {
            throw new ResponseStatusException("Restock quantity must be greater than zero", HttpStatus.BAD_REQUEST);
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

