package com.api.mithai.sweet.service;

import com.api.mithai.sweet.dto.SweetCategoryResponseDto;
import com.api.mithai.sweet.entity.SweetCategory;
import com.api.mithai.sweet.repository.SweetCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class SweetCategoryService {

    private final SweetCategoryRepository sweetCategoryRepository;

    public List<SweetCategoryResponseDto> getAllCategories() {
        return mapToResponseDto(sweetCategoryRepository.findAll());
    }

    public List<SweetCategoryResponseDto> mapToResponseDto(List<SweetCategory> sweetCategories){
        return sweetCategories.stream()
                .map((c) -> {
                    SweetCategoryResponseDto sweetCategoryResponseDto = new SweetCategoryResponseDto();
                    sweetCategoryResponseDto.setId(c.getId());
                    sweetCategoryResponseDto.setName(c.getName());
                    return sweetCategoryResponseDto;
                }).collect(Collectors.toList());
    }

}
