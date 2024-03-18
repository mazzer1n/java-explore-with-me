package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.*;

import java.util.List;

public interface CompilationServiceInterface {
    List<CompilationDto> findAll(Boolean pinned, Integer from, Integer size);

    CompilationDto findById(Long compId);

    CompilationDto save(NewCompilationDto dto);

    void delete(Long compId);

    CompilationDto update(Long compId, UpdateCompilationRequest dto);
}