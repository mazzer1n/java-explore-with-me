package ru.practicum.event.service.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.implementations.CategoryPublicService;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.states.StateGivenByAdmin;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.service.EventAdminServiceInterface;
import ru.practicum.exception.exceptions.EventBadRequestException;
import ru.practicum.exception.exceptions.EventConflictException;
import ru.practicum.exception.exceptions.EventNotFoundException;
import ru.practicum.location.model.Location;
import ru.practicum.location.service.LocationService;
import ru.practicum.request.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.category.dto.CategoryMapper.toCategoryFromCategoryDto;
import static ru.practicum.event.dto.EventMapper.toEventFullDto;
import static ru.practicum.event.model.states.EventLifecycleState.*;

@Service
@RequiredArgsConstructor
public class EventAdminService implements EventAdminServiceInterface {
    private final EventRepository eventRepository;
    private final RequestService requestService;
    private final CategoryPublicService categoryService;
    private final LocationService locationService;

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> findAllFullEventsByAdmin(
        List<Long> users,
        List<String> states,
        List<Long> categories,
        LocalDateTime rangeStart,
        LocalDateTime rangeEnd,
        Integer from,
        Integer size) {
        validateStartAndEndForGetQuery(rangeStart, rangeEnd);
        PageRequest page = PageRequest.of(from / size, size);
        Specification<Event> specification = Specification.where(null);

        if (users != null && !users.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                root.get("initiator").get("id").in(users));
        }

        if (states != null && !states.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                root.get("state").as(String.class).in(states));
        }

        if (categories != null && !categories.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                root.get("category").get("id").in(categories));
        }

        if (rangeStart != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        List<EventFullDto> events = getEventsBySpecification(specification, page);
        setConfirmedRequestsForEvents(events);

        return events;
    }

    @Override
    @Transactional
    public EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest dto) {
        Event updated = eventRepository.findById(eventId).orElseThrow(
            () -> new EventNotFoundException("Событие с id " + eventId + " не найдено.")
        );

        if (dto.getStateAction() != null) {
            String state = dto.getStateAction();
            updateStateByAdmin(updated, StateGivenByAdmin.valueOf(state));
        }

        if (dto.getEventDate() != null) {
            validateDateForUpdateByAdmin(dto.getEventDate());
            updated.setEventDate(dto.getEventDate());
        }

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            updated.setTitle(dto.getTitle());
        }

        if (dto.getAnnotation() != null && !dto.getAnnotation().isBlank()) {
            updated.setAnnotation(dto.getAnnotation());
        }

        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            updated.setDescription(dto.getDescription());
        }

        if (dto.getCategory() != null) {
            Category category = toCategoryFromCategoryDto(categoryService.findById(dto.getCategory()));
            updated.setCategory(category);
        }

        if (dto.getLocation() != null) {
            Location location = locationService.findByLatAndLon(dto.getLocation());
            updated.setLocation(location);
        }

        if (dto.getPaid() != null) {
            updated.setPaid(dto.getPaid());
        }

        if (dto.getParticipantLimit() != null) {
            updated.setParticipantLimit(dto.getParticipantLimit());
        }

        if (dto.getRequestModeration() != null) {
            updated.setRequestModeration(dto.getRequestModeration());
        }

        return toEventFullDto(eventRepository.save(updated));
    }

    private List<EventFullDto> getEventsBySpecification(Specification<Event> specification, Pageable pageable) {
        return eventRepository.findAll(specification, pageable).stream()
            .map(EventMapper::toEventFullDto)
            .collect(Collectors.toList());
    }

    private void setConfirmedRequestsForEvents(List<EventFullDto> events) {
        for (EventFullDto event : events) {
            Long count = requestService.getCountOfConfirmedRequestsForEvent(event.getId());
            event.setConfirmedRequests(count);
        }
    }

    private void validateStartAndEndForGetQuery(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        boolean startAndEndAreNotNull = rangeStart != null && rangeEnd != null;
        if (startAndEndAreNotNull && rangeStart.isAfter(rangeEnd)) {
            throw new EventBadRequestException("Начало события не может быть позже конца");
        }
    }

    private void validateDateForUpdateByAdmin(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new EventBadRequestException(
                "Дата начала изменяемого события должна быть не ранее чем за час от даты публикации"
            );
        }
    }

    private void updateStateByAdmin(Event updated, StateGivenByAdmin state) {
        if (state.equals(StateGivenByAdmin.PUBLISH_EVENT)) {
            if (!updated.getState().equals(PENDING)) {
                throw new EventConflictException(
                    "Событие можно публиковать, только если оно в состоянии ожидания публикации"
                );
            }

            updated.setState(PUBLISHED);
            updated.setPublishedOn(LocalDateTime.now());
        } else if (state.equals(StateGivenByAdmin.REJECT_EVENT)) {
            if (updated.getState().equals(PUBLISHED)) {
                throw new EventConflictException("Событие можно отклонить, только если оно еще не опубликовано");
            }

            updated.setState(CANCELED);
        }
    }
}