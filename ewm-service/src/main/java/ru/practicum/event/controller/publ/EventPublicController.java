package ru.practicum.event.controller.publ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.EventPublicServiceInterface;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class EventPublicController {
    private final EventPublicServiceInterface eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> findAllPublishedEvents(
        @RequestParam(required = false) String text,
        @RequestParam(required = false) List<Long> categories,
        @RequestParam(required = false) Boolean paid,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
        @RequestParam(defaultValue = "false") Boolean onlyAvailable,
        @RequestParam(defaultValue = "EVENT_DATE") String sort,
        @RequestParam(defaultValue = "0", required = false) Integer from,
        @RequestParam(defaultValue = "10", required = false) Integer size,
        HttpServletRequest request) {
        log.info("Получение событий с возможностью фильтрации");

        return eventService.findAllPublishedEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
            sort, from, size, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto findById(@PathVariable Long id, HttpServletRequest request) {
        log.info("Получение подробной информации об опубликованном событии по его идентификатору");

        return eventService.findById(id, request);
    }
}