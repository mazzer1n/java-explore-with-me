package ru.practicum.compilation.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.*;
import ru.practicum.compilation.service.CompilationServiceInterface;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationAdminController {
    private final CompilationServiceInterface compilationServiceInterface;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto save(@RequestBody @Valid NewCompilationDto dto) {
        log.info("Добавление новой подборки (подборка может не содержать события)");

        return compilationServiceInterface.save(dto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long compId) {
        log.info("Удаление подборки");
        compilationServiceInterface.delete(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto update(@PathVariable Long compId, @RequestBody @Valid UpdateCompilationRequest dto) {
        log.info("Обновить информацию о подборке");

        return compilationServiceInterface.update(compId, dto);
    }
}