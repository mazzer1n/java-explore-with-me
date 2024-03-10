package ru.practicum.category.controller.publ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryPublicServiceInterface;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryPublicController {
    private final CategoryPublicServiceInterface categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> findAll(
        @RequestParam(defaultValue = "0", required = false) Integer from,
        @RequestParam(defaultValue = "10", required = false) Integer size) {
        log.info("Получение категорий");

        return categoryService.findAll(from, size);
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto findById(@PathVariable Long catId) {
        log.info("Получение информации о категории по ID");

        return categoryService.findById(catId);
    }
}