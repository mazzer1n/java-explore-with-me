package ru.practicum.category.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.*;
import ru.practicum.category.service.CategoryAdminServiceInterface;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryAdminController {
    private final CategoryAdminServiceInterface categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto save(@RequestBody @Valid NewCategoryDto dto) {
        log.info("Добавление новой категории");

        return categoryService.save(dto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long catId) {
        log.info("Удаление категории");
        categoryService.delete(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto update(@PathVariable Long catId, @RequestBody @Valid NewCategoryDto dto) {
        log.info("Изменение категории");

        return categoryService.update(catId, dto);
    }
}