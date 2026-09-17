package com.enigmacamp.enigmaCademy.controller;

import com.enigmacamp.enigmaCademy.dto.request.CategoryRequest;
import com.enigmacamp.enigmaCademy.dto.response.CategoryResponse;
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

    // mapper category response
    private CategoryResponse convertToCategoryResponse(Category ca){
        return new CategoryResponse(ca.getId(), ca.getName(), ca.getDescription());
    }

    @PostMapping
    public Category save(@RequestBody Category category){
        return categoryService.save(category);
    }

    @PutMapping
    public CategoryResponse update(@RequestBody CategoryRequest categoryRequest) {
        // return categoryService.update(category);
//         Category category= new Category(categoryRequest);
         Category category= new Category(categoryRequest.getId(), categoryRequest.getName(), categoryRequest.getDescription());
        return convertToCategoryResponse(categoryService.update(category));
    }

    @GetMapping
    public List<Category> findByKeyword(@RequestParam String keyword){
        return categoryService.findByKeyword(keyword);
    }

}
