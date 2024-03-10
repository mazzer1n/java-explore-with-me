package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ViewStats;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.service.StatsServiceInterface;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsServiceInterface statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@RequestBody @Valid EndpointHitDto dto) {
        log.info("Сохранение запроса пользователем");
        statsService.saveHit(dto);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStats> getViewStats(
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
        @RequestParam(required = false) List<String> uris,
        @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("Получение статистики по посещениям");

        return statsService.getViewStats(start, end, uris, unique);
    }
}