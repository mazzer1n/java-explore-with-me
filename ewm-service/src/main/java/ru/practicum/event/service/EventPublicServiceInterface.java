package ru.practicum.event.service;

import ru.practicum.event.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventPublicServiceInterface {
    List<EventShortDto> findAllPublishedEvents(
        String text,
        List<Long> categories,
        Boolean paid,
        LocalDateTime rangeStart,
        LocalDateTime rangeEnd,
        Boolean onlyAvailable,
        String sort,
        Integer from,
        Integer size,
        HttpServletRequest request);

    EventFullDto findById(Long id, HttpServletRequest request);
}