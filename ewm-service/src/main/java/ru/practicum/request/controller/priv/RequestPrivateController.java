package ru.practicum.request.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestServiceInterface;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestPrivateController {
    private final RequestServiceInterface requestServiceInterface;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> findAllRequestsForOtherEvents(@PathVariable Long userId) {
        log.info("Получение информации о заявках текущего пользователя на участие в чужих событиях");

        return requestServiceInterface.findAllRequestsForOtherEvents(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto save(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("Добавление запроса от текущего пользователя на участие в событии");

        return requestServiceInterface.save(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Отмена своего запроса на участие в событии");

        return requestServiceInterface.cancelRequest(userId, requestId);
    }
}