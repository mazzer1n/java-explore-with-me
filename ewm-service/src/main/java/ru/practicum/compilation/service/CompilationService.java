package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.*;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.exception.exceptions.CompilationNotFoundException;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.implementations.EventPublicService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.compilation.dto.CompilationMapper.*;

@Service
@RequiredArgsConstructor
public class CompilationService implements CompilationServiceInterface {
    private final CompilationRepository compilationRepository;
    private final EventPublicService eventService;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> findAll(Boolean pinned, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);

        return compilationRepository.findAllByPinned(pinned, page).stream()
            .map(CompilationMapper::toCompilationDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto findById(Long compId) {
        Compilation compilation = getExistingCompilation(compId);

        return toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto save(NewCompilationDto dto) {
        Compilation compilation = toCompilation(dto);

        if (dto.getPinned() == null) {
            compilation.setPinned(false);
        }

        if (dto.getEvents() != null) {
            List<Event> events = eventService.findAllEventsWithIdIn(dto.getEvents());
            compilation.setEvents(events);
        }

        return toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationRequest dto) {
        Compilation updated = getExistingCompilation(compId);

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            updated.setTitle(dto.getTitle());
        }

        if (dto.getPinned() != null) {
            updated.setPinned(dto.getPinned());
        }

        if (dto.getEvents() != null) {
            List<Event> events = eventService.findAllEventsWithIdIn(dto.getEvents());
            updated.setEvents(events);
        }

        return toCompilationDto(compilationRepository.save(updated));
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        getExistingCompilation(compId);
        compilationRepository.deleteById(compId);
    }

    public Compilation getExistingCompilation(long compId) {
        return compilationRepository.findById(compId).orElseThrow(
            () -> new CompilationNotFoundException("Подборка событий с id " + compId + " не найдена")
        );
    }
}