package com.api.mithai.sweet.specification;

import com.api.mithai.sweet.entity.Sweet;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class SweetSpecification {

    public static Specification<Sweet> withFilters(String searchValue, Double minPrice, Double maxPrice) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            List<Predicate> orPredicates = new ArrayList<>();

            Join<Object, Object> categoryJoin = root.join("category", JoinType.INNER);

            // Search by name (case-insensitive, contains) - OR condition
            if (searchValue != null && !searchValue.trim().isEmpty()) {
                Predicate namePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + searchValue.toLowerCase().trim() + "%"
                );
                orPredicates.add(namePredicate);

                // Also search in category name if searchValue is provided
                if (categoryJoin != null) {
                    Predicate categorySearchPredicate = criteriaBuilder.like(
                            criteriaBuilder.lower(categoryJoin.get("name")),
                            "%" + searchValue.toLowerCase().trim() + "%"
                    );
                    orPredicates.add(categorySearchPredicate);
                }
            }

            // Combine OR predicates (name OR category name)
            if (!orPredicates.isEmpty()) {
                predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
            }

            // Price range filter (AND condition)
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            return predicates.isEmpty() ? 
                    criteriaBuilder.conjunction() : 
                    criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

