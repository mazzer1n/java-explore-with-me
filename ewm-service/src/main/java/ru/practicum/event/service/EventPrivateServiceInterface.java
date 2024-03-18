package ru.practicum.event.service;

import ru.practicum.event.dto.*;

import java.util.List;

public interface EventPrivateServiceInterface {
    List<EventShortDto> findUserEvents(Long userId, Integer from, Integer size);

    EventFullDto findUserFullEvent(Long userId, Long eventId);

    EventFullDto save(Long userId, NewEventDto dto);

    EventFullDto updateByUser(Long userId, Long eventId, UpdateEventUserRequest dto);
}