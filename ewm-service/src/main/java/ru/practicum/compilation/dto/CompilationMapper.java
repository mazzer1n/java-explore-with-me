package ru.practicum.compilation.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventMapper;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {
    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
            .title(compilation.getTitle())
            .pinned(compilation.getPinned())
            .id(compilation.getId())
            .events(compilation.getEvents().stream().map(EventMapper::toEventShortDto).collect(Collectors.toList()))
            .build();
    }

    public static Compilation toCompilation(NewCompilationDto dto) {
        return Compilation.builder()
            .pinned(dto.getPinned())
            .title(dto.getTitle())
            .events(List.of())
            .build();
    }
}