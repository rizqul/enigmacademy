package com.enigmacamp.enigmaCademy.controller;

import com.enigmacamp.enigmaCademy.model.Category;
import com.enigmacamp.enigmaCademy.service.CategoryService;
import com.enigmacamp.enigmaCademy.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping
    public Category update(@RequestBody Category category) {
        return categoryService.update(category);
    }

    @GetMapping
    public List<Category> findByKeyword(@RequestParam String keyword){
        return categoryService.findByKeyword(keyword);
    }

}
