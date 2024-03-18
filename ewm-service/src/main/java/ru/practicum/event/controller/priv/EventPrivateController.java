package ru.practicum.event.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.EventPrivateServiceInterface;
import ru.practicum.request.dto.*;
import ru.practicum.request.service.RequestServiceInterface;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class EventPrivateController {
    private final EventPrivateServiceInterface eventServiceInterface;
    private final RequestServiceInterface requestServiceInterface;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto save(@PathVariable Long userId, @RequestBody @Valid NewEventDto dto) {
        log.info("Добавление нового события");

        return eventServiceInterface.save(userId, dto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> findUserEvents(@PathVariable Long userId,
        @RequestParam(defaultValue = "0", required = false) Integer from,
        @RequestParam(defaultValue = "10", required = false) Integer size) {
        log.info("Получение событий, добавленных текущим пользователем");

        return eventServiceInterface.findUserEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto findUserFullEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Получение полной информации о событии добавленном текущим пользователем");

        return eventServiceInterface.findUserFullEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateByUser(
        @PathVariable Long userId,
        @PathVariable Long eventId,
        @RequestBody @Valid UpdateEventUserRequest dto) {
        log.info("Изменение события добавленного текущим пользователем");

        return eventServiceInterface.updateByUser(userId, eventId, dto);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> findEventRequestsByOwner(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Получение информации о запросах на участие в событии текущего пользователя");

        return requestServiceInterface.findEventRequestsByOwner(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateEventRequestsStatuses(
        @PathVariable Long userId,
        @PathVariable Long eventId,
        @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя");

        return requestServiceInterface.updateEventRequestsStatuses(userId, eventId, request);
    }
}