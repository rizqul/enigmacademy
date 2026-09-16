package com.enigmacamp.enigmaCademy.service.impl;

import com.enigmacamp.enigmaCademy.model.Category;
import com.enigmacamp.enigmaCademy.repository.CategoryRepository;
import com.enigmacamp.enigmaCademy.service.CategoryService;
import com.enigmacamp.enigmaCademy.utils.exception.DuplicateNameException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    @Override
    public Optional<Category> findById(Long id) {

        if(id == null || id <= 0) {
            throw new IllegalArgumentException("ID Tidak Boleh Kosong atau Lebih kecil/sama dengan nol");
        }

        return categoryRepository.findById(id);
    }

    @Override
    @Transactional
    public Category update(Category category) {

        if (category.getName()==null){
            throw new NullPointerException("Nama Tidak Boleh Null!");
        }

        if (category.getDescription()==null){
            throw new NullPointerException("Description Tidak Boleh Null!");
        }

//        if (categoryRepository.existsByName(category.getName())){
//            throw new DuplicateNameException("Nama Tidak Boleh Sama!");
//        }

        Optional<Category> oldCategory = findById(category.getId());

        if (oldCategory.isEmpty()) {
            throw new IllegalArgumentException("Id Tidak Ditemukan");
        }

        Category updateCategory = oldCategory.get();

        if (!updateCategory.getName().equals(category.getName())) {
            updateCategory.setName(category.getName());
        }

        updateCategory.setDescription(category.getDescription());

        return categoryRepository.save(updateCategory);

    }
}
