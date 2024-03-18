package ru.practicum.event.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.EventAdminServiceInterface;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
public class EventAdminController {
    private final EventAdminServiceInterface eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> findAllFullEventsByAdmin(
        @RequestParam(required = false) List<Long> users,
        @RequestParam(required = false) List<String> states,
        @RequestParam(required = false) List<Long> categories,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
        @RequestParam(defaultValue = "0", required = false) Integer from,
        @RequestParam(defaultValue = "10", required = false) Integer size) {
        log.info("Поиск событий");

        return eventService.findAllFullEventsByAdmin(
            users, states, categories, rangeStart, rangeEnd, from, size
        );
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateByAdmin(@PathVariable Long eventId, @RequestBody @Valid UpdateEventAdminRequest dto) {
        log.info("Редактирование данных события и его статуса (отклонение/публикация)");

        return eventService.updateByAdmin(eventId, dto);
    }
}