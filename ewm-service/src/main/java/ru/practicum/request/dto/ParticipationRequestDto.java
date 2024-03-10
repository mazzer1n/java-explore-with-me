package ru.practicum.request.dto;

import lombok.*;
import ru.practicum.request.model.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ParticipationRequestDto {
    private Long id;
    private RequestStatus status;
    private Long event;
    private Long requester;
    private LocalDateTime created;
}