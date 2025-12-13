package com.api.mithai.sweet.repository;

import com.api.mithai.sweet.entity.Sweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SweetRepository extends JpaRepository<Sweet, Long> {
    boolean existsByNameIgnoreCase(String name);
}

