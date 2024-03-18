package ru.practicum.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.request.model.ParticipationRequest;

@UtilityClass
public class RequestMapper {
    public static ParticipationRequestDto toRequestDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
            .id(request.getId())
            .event(request.getEvent().getId())
            .requester(request.getRequester().getId())
            .created(request.getCreated())
            .status(request.getStatus())
            .build();
    }
}