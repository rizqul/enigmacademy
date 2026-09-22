package com.enigmacamp.enigmaCademy.specification;

import com.enigmacamp.enigmaCademy.dto.request.SearchUserRequest;
import com.enigmacamp.enigmaCademy.model.User;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static Specification<User> getSpecification(SearchUserRequest request) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // filtering atau search berdasarkan name
            if (request.getName() != null && !request.getName().isEmpty()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("name")),
                                "%" + request.getName().toLowerCase() + "%"));
            }

            // filtering atau search berdasarkan username
            if (request.getUsername() != null && !request.getUsername().isEmpty()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("username")),
                                "%" + request.getUsername().toLowerCase() + "%"));
            }

            // filtering atau searcj berdasarkan email
            if (request.getEmail() != null && !request.getEmail().isEmpty()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("email")),
                                "%" + request.getEmail().toLowerCase() + "%"));
            }

            // Filtering By Created At
            if (request.getCreatedAt() != null && !request.getCreatedAt().isEmpty()) {
                LocalDateTime searchTime = LocalDateTime.of(LocalDate.parse(request.getCreatedAt()),
                        LocalTime.of(0, 0));

                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), searchTime));
            }

            // Filtering By UserRole
            String userRole = request.getUserRole().toUpperCase();

            if (!request.getUserRole().isEmpty()) {
                if (UserRole.isValid(userRole)) {
                    userRole = UserRole.valueOf(userRole).toString().toUpperCase();
                }

                predicates.add(
                        criteriaBuilder.equal(root.get("userRole"), userRole));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
