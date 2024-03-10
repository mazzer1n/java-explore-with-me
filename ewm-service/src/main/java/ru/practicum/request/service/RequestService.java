package ru.practicum.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.implementations.EventPrivateService;
import ru.practicum.exception.exceptions.ParticiopationBadRequestException;
import ru.practicum.exception.exceptions.RequestConflictException;
import ru.practicum.request.dto.*;
import ru.practicum.request.model.*;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.request.dto.RequestMapper.toRequestDto;
import static ru.practicum.request.model.RequestStatus.*;

@Service
public class RequestService implements RequestServiceInterface {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventPrivateService eventService;

    @Autowired
    public RequestService(RequestRepository requestRepository,
        @Lazy UserService userService,
        @Lazy EventPrivateService eventService) {
        this.requestRepository = requestRepository;
        this.userService = userService;
        this.eventService = eventService;
    }

    @Override
    @Transactional
    public ParticipationRequestDto save(Long userId, Long eventId) {
        LocalDateTime now = LocalDateTime.now();
        User user = userService.getExistingUser(userId);
        Event event = eventService.getExistingEvent(eventId);
        validatePropertiesForCreate(userId, event);

        ParticipationRequest request = ParticipationRequest.builder()
            .event(event)
            .requester(user)
            .created(now)
            .build();

        setStatusWhenCreate(request, event);

        return toRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, userId);
        request.setStatus(CANCELED);

        return toRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateEventRequestsStatuses(Long userId, Long eventId,
        EventRequestStatusUpdateRequest request) {

        Event event = eventService.getExistingEvent(eventId);
        long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, CONFIRMED);
        validateForUpdate(userId, event, confirmedRequests);

        List<ParticipationRequest> requests = requestRepository.findAllByIdInAndEventIdAndStatus(
            request.getRequestIds(), eventId, PENDING);
        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            ParticipationRequest updated = requests.get(i);
            if (request.getStatus() == REJECTED) {
                updated.setStatus(REJECTED);
                rejected.add(toRequestDto(updated));
            }

            if (request.getStatus() == CONFIRMED && event.getParticipantLimit() > 0 &&
                (confirmedRequests + i) < event.getParticipantLimit()) {
                updated.setStatus(CONFIRMED);
                confirmed.add(toRequestDto(updated));
            } else {
                updated.setStatus(REJECTED);
                rejected.add(toRequestDto(updated));
            }
        }

        return new EventRequestStatusUpdateResult(confirmed, rejected);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> findAllRequestsForOtherEvents(Long userId) {
        userService.getExistingUser(userId);

        return requestRepository.findAllByRequesterId(userId).stream()
            .map(RequestMapper::toRequestDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> findEventRequestsByOwner(Long userId, Long eventId) {
        userService.getExistingUser(userId);
        eventService.findUserFullEvent(userId, eventId);

        return requestRepository.findAllByEventId(eventId).stream()
            .map(RequestMapper::toRequestDto)
            .collect(Collectors.toList());
    }

    public List<ParticipationRequest> findConfirmedRequests(List<Long> eventsIds, RequestStatus status) {
        return requestRepository.findAllByEventIdInAndStatus(eventsIds, status);
    }

    public Long getCountOfConfirmedRequestsForEvent(Long eventId) {
        return requestRepository.countByEventIdAndStatus(eventId, CONFIRMED);
    }

    private void validatePropertiesForCreate(Long userId, Event event) {
        if (userId.equals(event.getInitiator().getId())) {
            throw new RequestConflictException(
                "Инициатор события не может добавить запрос на участие в своём событии"
            );
        }

        if (requestRepository.existsByEventIdAndRequesterId(event.getId(), userId)) {
            throw new RequestConflictException(
                "Нельзя добавить повторный запрос"
            );
        }

        if (!event.getState().toString().equals("PUBLISHED")) {
            throw new RequestConflictException("Нельзя участвовать в неопубликованном событии");
        }

        int limit = event.getParticipantLimit();
        boolean isLimitReached = limit <= requestRepository.countByEventIdAndStatus(event.getId(), CONFIRMED);
        if (limit != 0 && isLimitReached) {
            throw new RequestConflictException("У события достигнут лимит запросов на участие");
        }
    }

    private void setStatusWhenCreate(ParticipationRequest request, Event event) {
        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            request.setStatus(PENDING);
        } else {
            request.setStatus(CONFIRMED);
        }
    }

    private void validateForUpdate(Long userId, Event event, long confirmedRequests) {
        userService.getExistingUser(userId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ParticiopationBadRequestException(
                "Пользователь с id " + userId + " не является организатором события"
            );
        }

        validateParticipantLimit(event, confirmedRequests);
    }

    private void validateParticipantLimit(Event event, long confirmedRequests) {
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() <= confirmedRequests) {
            throw new RequestConflictException("Количество участников на событие ограничено");
        }
    }
}