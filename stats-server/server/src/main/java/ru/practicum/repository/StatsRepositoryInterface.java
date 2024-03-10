package ru.practicum.repository;

import ru.practicum.dto.*;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepositoryInterface {
    void save(EndpointHit dto);

    List<ViewStats> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    List<ViewStats> getUniqueViewStats(LocalDateTime start, LocalDateTime end, List<String> uris);
}
