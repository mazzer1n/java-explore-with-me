package ru.practicum.event.service.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.implementations.CategoryPublicService;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.states.EventLifecycleState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.service.EventPrivateServiceInterface;
import ru.practicum.exception.exceptions.EventBadRequestException;
import ru.practicum.exception.exceptions.EventConflictException;
import ru.practicum.exception.exceptions.EventNotFoundException;
import ru.practicum.location.model.Location;
import ru.practicum.location.service.LocationService;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.category.dto.CategoryMapper.toCategoryFromCategoryDto;
import static ru.practicum.event.dto.EventMapper.*;
import static ru.practicum.event.model.states.EventLifecycleState.*;
import static ru.practicum.event.model.states.StateGivenByUser.*;

@Service
@RequiredArgsConstructor
public class EventPrivateService implements EventPrivateServiceInterface {
    private final UserService userService;
    private final CategoryPublicService categoryService;
    private final LocationService locationService;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public EventFullDto save(Long userId, NewEventDto dto) {
        User user = userService.getExistingUser(userId);
        validateDateForUpdateAndCreateByUser(dto.getEventDate());

        Event event = toEvent(dto);
        setPropertiesWhenCreating(user, dto, event);

        return toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> findUserEvents(Long userId, Integer from, Integer size) {
        userService.getExistingUser(userId);
        Pageable pageable = PageRequest.of(from / size, size);

        List<EventShortDto> result = eventRepository.findAllByInitiatorId(userId, pageable).stream()
            .map(EventMapper::toEventShortDto)
            .collect(Collectors.toList());

        result = result.isEmpty() ? new ArrayList<>() : result;

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto findUserFullEvent(Long userId, Long eventId) {
        userService.getExistingUser(userId);

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (event == null) {
            throw new EventNotFoundException("Событие с id " + eventId + " не найдено.");
        }

        return toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto updateByUser(Long userId, Long eventId, UpdateEventUserRequest dto) {
        userService.getExistingUser(userId);
        Event updated = getExistingEvent(eventId);
        if (!updated.getInitiator().getId().equals(userId)) {
            throw new EventBadRequestException("Данные события может изменить только его организатор");
        }

        validateStateForUpdateByUser(updated.getState());
        updateState(dto.getStateAction(), updated);

        if (dto.getEventDate() != null) {
            validateDateForUpdateAndCreateByUser(dto.getEventDate());
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

    public Event getExistingEvent(long id) {
        return eventRepository.findById(id).orElseThrow(
            () -> new EventNotFoundException("Событие с id " + id + " не найдено.")
        );
    }

    private void validateDateForUpdateAndCreateByUser(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventBadRequestException(
                "Дата начала изменяемого события должна быть не ранее чем за 2 часа от даты публикации"
            );
        }
    }

    private void setPropertiesWhenCreating(User user, NewEventDto dto, Event event) {
        Category category = toCategoryFromCategoryDto(categoryService.findById(dto.getCategory()));
        Location location = locationService.findByLatAndLon(dto.getLocation());

        event.setInitiator(user);
        event.setCategory(category);
        event.setLocation(location);
        event.setState(PENDING);
        event.setCreatedOn(LocalDateTime.now());
    }

    private void validateStateForUpdateByUser(EventLifecycleState state) {
        if (state.equals(PUBLISHED)) {
            throw new EventConflictException(
                "Изменить можно только отмененные события или события в состоянии ожидания модерации"
            );
        }
    }

    private void updateState(String state, Event event) {
        if (state != null) {
            if (state.equals(String.valueOf(SEND_TO_REVIEW))) {
                event.setState(PENDING);
            } else if (state.equals(String.valueOf(CANCEL_REVIEW))) {
                event.setState(CANCELED);
            }
        }
    }
}