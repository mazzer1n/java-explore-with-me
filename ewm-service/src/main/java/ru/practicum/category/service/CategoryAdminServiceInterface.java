package ru.practicum.category.service;

import ru.practicum.category.dto.*;

public interface CategoryAdminServiceInterface {
    CategoryDto save(NewCategoryDto dto);

    void delete(Long catId);

    CategoryDto update(Long catId, NewCategoryDto dto);
}