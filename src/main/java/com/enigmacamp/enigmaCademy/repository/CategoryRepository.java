package com.enigmacamp.enigmaCademy.repository;

import com.enigmacamp.enigmaCademy.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    boolean existsByName(String name);
    List<Category> findByNameContaining(String name);
    List<Category> findByDescriptionContaining(String description);
}
