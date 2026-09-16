package com.enigmacamp.enigmaCademy.controller;

import com.enigmacamp.enigmaCademy.model.Category;
import com.enigmacamp.enigmaCademy.service.CategoryService;
import com.enigmacamp.enigmaCademy.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public Category save(@RequestBody Category category){
        return categoryService.save(category);
    }

}
