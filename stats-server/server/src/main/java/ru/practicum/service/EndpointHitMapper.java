package ru.practicum.service;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.model.EndpointHit;

@UtilityClass
public class EndpointHitMapper {
    public static EndpointHit dtoToEndpointHit(EndpointHitDto dto) {
        return EndpointHit.builder()
            .id(dto.getId())
            .app(dto.getApp())
            .ip(dto.getIp())
            .uri(dto.getUri())
            .timestamp(dto.getTimestamp())
            .build();
    }
}