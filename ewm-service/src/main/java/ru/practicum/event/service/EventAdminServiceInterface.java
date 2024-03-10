package ru.practicum.event.service;

import ru.practicum.event.dto.*;

import java.time.LocalDateTime;
import java.util.List;

public interface EventAdminServiceInterface {
    List<EventFullDto> findAllFullEventsByAdmin(
        List<Long> users,
        List<String> states,
        List<Long> categories,
        LocalDateTime rangeStart,
        LocalDateTime rangeEnd,
        Integer from,
        Integer size);

    EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest dto);
}