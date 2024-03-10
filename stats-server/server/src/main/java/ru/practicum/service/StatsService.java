package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStats;
import ru.practicum.exception.exceptions.ViewStatsBadRequestException;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService implements StatsServiceInterface {
    private final StatsRepository statsRepository;

    @Transactional
    @Override
    public void saveHit(EndpointHitDto dto) {
        EndpointHit hit = EndpointHitMapper.dtoToEndpointHit(dto);
        statsRepository.save(hit);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ViewStats> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new ViewStatsBadRequestException("Старт не может быть после конца");
        }

        if (unique) {
            return statsRepository.getUniqueViewStats(start, end, uris);
        } else {
            return statsRepository.getViewStats(start, end, uris);
        }
    }
}