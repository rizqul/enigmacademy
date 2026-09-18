package com.enigmacamp.enigmaCademy.specification;

import com.enigmacamp.enigmaCademy.dto.request.SearchUserRequest;
import com.enigmacamp.enigmaCademy.model.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static Specification<User> getSpecification(SearchUserRequest request){
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // search by name
            if (request.getName() != null && !request.getName().isEmpty()){
                predicates.add(
                        criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("name")),
                                "%" + request.getName().toLowerCase() + "%"
                        )
                );
            }

            // search by email
            if (request.getEmail() != null && !request.getEmail().isEmpty()){
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("email")),
                                "%" + request.getEmail() + "%"
                        )
                );
            }

            // search by role
            // search by create at

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
