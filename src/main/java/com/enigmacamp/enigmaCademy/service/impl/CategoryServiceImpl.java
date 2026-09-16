package com.enigmacamp.enigmaCademy.service.impl;

import com.enigmacamp.enigmaCademy.model.Category;
import com.enigmacamp.enigmaCademy.repository.CategoryRepository;
import com.enigmacamp.enigmaCademy.service.CategoryService;
import com.enigmacamp.enigmaCademy.utils.exception.DuplicateNameException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    public final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public Category save(Category category) {
        if (category.getName()==null){
            throw new NullPointerException("Nama Tidak Boleh Null!");
        }
        if (category.getDescription()==null){
            throw new NullPointerException("Description Tidak Boleh Null!");
        }
        if (categoryRepository.existsByName(category.getName())){
            throw new DuplicateNameException("Nama Sudah Ada!");
        }
        return categoryRepository.save(category);
    }
}
