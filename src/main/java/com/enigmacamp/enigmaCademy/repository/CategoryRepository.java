package com.enigmacamp.enigmaCademy.repository;

import com.enigmacamp.enigmaCademy.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
}
