package com.enigmacamp.enigmaCademy.service.impl;

import com.enigmacamp.enigmaCademy.dto.request.SearchCategoryRequest;
import com.enigmacamp.enigmaCademy.dto.response.CategoryResponse;
import com.enigmacamp.enigmaCademy.mapper.CategoryMapper;
import com.enigmacamp.enigmaCademy.model.Category;
import com.enigmacamp.enigmaCademy.repository.CategoryRepository;
import com.enigmacamp.enigmaCademy.service.CategoryService;
import com.enigmacamp.enigmaCademy.specification.CategorySpecification;
import com.enigmacamp.enigmaCademy.utils.exception.DuplicateNameException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    public Category update(Long id, Category category) {

        if (category.getName()==null){
            throw new NullPointerException("Nama Tidak Boleh Null!");
        }

        if (category.getDescription()==null){
            throw new NullPointerException("Description Tidak Boleh Null!");
        }

        Optional<Category> oldCategory = findById(id);

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

    @Override
    public List<Category> findByKeyword(String keyword){
        if(keyword.trim().isEmpty()){
            throw new NullPointerException("keyword tidak boleh kosong");
        }

        List<Category> categories = categoryRepository.findByNameContaining(keyword);
        List<Category> categories2 = categoryRepository.findByDescriptionContaining(keyword);

        Set<Category> newHashSet = new HashSet<>();
        newHashSet.addAll(categories);
        newHashSet.addAll(categories2);

        return newHashSet.stream().toList();

    }

    @Override
    public List<CategoryResponse> searchCategories(SearchCategoryRequest request) {
        Specification<Category> specification = CategorySpecification.getSpacification(request);

        List<Category> categories = categoryRepository.findAll(specification);

        return CategoryMapper.convertToListOfCategoryResponse(categories);
    }
}
