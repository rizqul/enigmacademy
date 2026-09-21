package com.enigmacamp.enigmaCademy.controller;

import com.enigmacamp.enigmaCademy.dto.request.CategoryRequest;
import com.enigmacamp.enigmaCademy.dto.request.SearchCategoryRequest;
import com.enigmacamp.enigmaCademy.dto.response.CategoryResponse;
import com.enigmacamp.enigmaCademy.dto.response.CommonResponse;
import com.enigmacamp.enigmaCademy.model.Category;
import com.enigmacamp.enigmaCademy.service.CategoryService;
import com.enigmacamp.enigmaCademy.service.UserService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public Category save(@Valid @RequestBody Category category){
        return categoryService.save(category);
    }

//    @PutMapping("/{id}")
//    public CategoryResponse update(@PathVariable Long id , @RequestBody CategoryRequest categoryRequest) {
//         Category category= new Category(categoryRequest.getName(), categoryRequest.getDescription());
//        return convertToCategoryResponse(categoryService.update(id, category));
//    }
//
//    // bikin ini
//    @GetMapping("/keyword")
//    public List<CategoryResponse> findByKeyword(@RequestParam String keyword){
//        return convertToListOfCategoryResponse(categoryService.findByKeyword(keyword));
//    }

    @GetMapping("/search")
    public ResponseEntity<CommonResponse<List<CategoryResponse>>> searchCategory (
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description
    ) {
        SearchCategoryRequest request =SearchCategoryRequest.builder()
                .name(name)
                .description(description)
                .build();

        List<CategoryResponse> categoryResponses = categoryService.searchCategories(request);

        CommonResponse<List<CategoryResponse>> response = CommonResponse.<List<CategoryResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Data berhasil ditampikan")
                .data(categoryResponses)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
