package com.api.mithai.sweet.repository;

import com.api.mithai.sweet.entity.SweetCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SweetCategoryRepository extends JpaRepository<SweetCategory, Long> {
    boolean existsByName(String name);
}

