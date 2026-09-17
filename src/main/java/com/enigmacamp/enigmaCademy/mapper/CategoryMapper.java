package com.enigmacamp.enigmaCademy.mapper;

import com.enigmacamp.enigmaCademy.dto.response.CategoryResponse;
import com.enigmacamp.enigmaCademy.model.Category;

import java.util.List;

public class CategoryMapper {
    // mapper category response
    public static CategoryResponse convertToCategoryResponse(Category ca){
        return new CategoryResponse(ca.getId(), ca.getName(), ca.getDescription());
    }

    // mapper find by keyword
    public static List<CategoryResponse> convertToListOfCategoryResponse(List<Category> categories) {
        return categories.stream()
                .map(CategoryMapper::convertToCategoryResponse)
                .toList();
    }
}
