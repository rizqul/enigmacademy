package com.enigmacamp.enigmaCademy.service;

import com.enigmacamp.enigmaCademy.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Category save(Category category);
    Optional<Category> findById(Long id);
    Category update(Long id, Category category);

    List<Category> findByKeyword(String keyword);
}
