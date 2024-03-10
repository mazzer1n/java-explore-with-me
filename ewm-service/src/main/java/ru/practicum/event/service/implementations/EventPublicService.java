package ru.practicum.event.service.implementations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatClient;
import ru.practicum.dto.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.service.EventPublicServiceInterface;
import ru.practicum.exception.exceptions.EventBadRequestException;
import ru.practicum.exception.exceptions.EventNotFoundException;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.service.RequestService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.event.dto.EventMapper.toEventFullDto;
import static ru.practicum.event.model.states.EventLifecycleState.PUBLISHED;
import static ru.practicum.request.model.RequestStatus.CONFIRMED;

@Service
@RequiredArgsConstructor
public class EventPublicService implements EventPublicServiceInterface {
    private final RequestService requestService;
    private final EventRepository eventRepository;
    private final StatClient statClient;
    @Value("${server.application.name:ewm-service}")
    private String applicationName;
    private final ObjectMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public EventFullDto findById(Long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id).orElseThrow(
            () -> new EventNotFoundException("Событие с id " + id + " не найдено.")
        );

        validatePublishedEventsForGet(event);
        ResponseEntity<Object> response = statClient.getViewStats(
            event.getCreatedOn(),
            LocalDateTime.now(),
            List.of(request.getRequestURI()),
            true
        );

        EventFullDto result = toEventFullDto(event);
        List<ViewStats> viewStats = mapper.convertValue(response.getBody(), new TypeReference<>() {});
        if (!viewStats.isEmpty()) {
            result.setViews(viewStats.get(0).getHits() + 1L);
        } else {
            result.setViews(1L);
        }

        saveHitForStatistic(request);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> findAllPublishedEvents(
        String text,
        List<Long> categories,
        Boolean paid,
        LocalDateTime rangeStart,
        LocalDateTime rangeEnd,
        Boolean onlyAvailable,
        String sort,
        Integer from,
        Integer size,
        HttpServletRequest request) {

        validateStartAndEndForGetQuery(rangeStart, rangeEnd);
        Specification<Event> specification = getSpecification(text, categories, paid, rangeStart, rangeEnd, onlyAvailable);
        Pageable page = getPageable(sort, from, size);

        List<Event> events = eventRepository.findAll(specification, page);
        List<EventShortDto> result = events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
        Map<Long, Long> viewStats = getViewStats(events);

        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        List<ParticipationRequest> confirmedRequests = requestService.findConfirmedRequests(ids, CONFIRMED);

        for (EventShortDto eventShortDto : result) {
            List<ParticipationRequest> eventRequests = confirmedRequests.stream()
                .filter(r -> Objects.equals(r.getEvent().getId(), eventShortDto.getId()))
                .collect(Collectors.toList());
            eventShortDto.setConfirmedRequests((long) eventRequests.size());

            Long eventViews = viewStats.getOrDefault(eventShortDto.getId(), 0L);
            eventShortDto.setViews(eventViews + 1L);
        }

        saveHitForStatistic(request);

        return result;
    }

    public Integer getCountOfEventsByCategory(Long catId) {
        return eventRepository.findAllByCategoryId(catId).size();
    }

    public List<Event> findAllEventsWithIdIn(List<Long> ids) {
        return eventRepository.findAllEventsByIdIn(ids);
    }

    private void validatePublishedEventsForGet(Event event) {
        if (!event.getState().equals(PUBLISHED)) {
            throw new EventNotFoundException("В выдаче должны быть только опубликованные события");
        }
    }

    private void saveHitForStatistic(HttpServletRequest request) {
        statClient.saveHit(EndpointHitDto.builder()
            .timestamp(LocalDateTime.now())
            .ip(request.getRemoteAddr())
            .app(applicationName)
            .uri(request.getRequestURI())
            .build()
        );
    }

    private void validateStartAndEndForGetQuery(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        boolean startAndEndAreNotNull = rangeStart != null && rangeEnd != null;
        if (startAndEndAreNotNull && rangeStart.isAfter(rangeEnd)) {
            throw new EventBadRequestException("Начало события не может быть позже конца");
        }
    }

    private Map<Long, Long> getViewStats(List<Event> events) {
        List<String> uris = events.stream()
            .map(event -> String.format("/events/" + event.getId()))
            .collect(Collectors.toList());

        LocalDateTime start = events.stream()
            .map(Event::getCreatedOn)
            .min(LocalDateTime::compareTo)
            .orElse(null);

        Map<Long, Long> viewStats = new HashMap<>();

        if (start != null) {
            ResponseEntity<Object> response = statClient.getViewStats(
                start,
                LocalDateTime.now(),
                uris,
                true
            );

            List<ViewStats> stats = mapper.convertValue(response.getBody(), new TypeReference<>() {});

            viewStats = stats.stream()
                .filter(dto -> dto.getUri().startsWith("/events/"))
                .collect(Collectors.toMap(
                    dto -> Long.parseLong(dto.getUri().substring("/events/".length())),
                    ViewStats::getHits
                ));
        }

        return viewStats;
    }

    private Pageable getPageable(String sort, Integer from, Integer size) {
        PageRequest page;

        if (sort.equals("EVENT_DATE")) {
            page = PageRequest.of(from / size, size, Sort.by("eventDate"));
        } else if (sort.equals("VIEWS")) {
            page = PageRequest.of(from / size, size, Sort.by("views").descending());
        } else {
            throw new EventBadRequestException("Сортировка может быть задана либо по просмотрам, либо по дате события");
        }

        return page;
    }

    private Specification<Event> getSpecification(String text, List<Long> categories, Boolean paid,
        LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable) {

        Specification<Event> specification = Specification.where(null);

        if (text != null && !text.isBlank()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + text.toLowerCase() + "%")
                ));
        }

        if (categories != null && !categories.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                root.get("category").get("id").in(categories));
        }

        if (paid != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("paid"), paid));
        }

        if (rangeStart != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        if (onlyAvailable != null && onlyAvailable) {
            specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0));
        }

        specification = specification.and((root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("state"), PUBLISHED));

        return specification;
    }
}