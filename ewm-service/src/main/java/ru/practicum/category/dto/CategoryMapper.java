package ru.practicum.category.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.category.model.Category;

@UtilityClass
public class CategoryMapper {
    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
            .id(category.getId())
            .name(category.getName())
            .build();
    }

    public static Category toCategoryFromNewCategoryDto(NewCategoryDto dto) {
        return Category.builder()
            .name(dto.getName())
            .build();
    }

    public static Category toCategoryFromCategoryDto(CategoryDto dto) {
        return Category.builder()
            .id(dto.getId())
            .name(dto.getName())
            .build();
    }
}