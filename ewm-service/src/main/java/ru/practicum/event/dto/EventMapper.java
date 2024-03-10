package ru.practicum.event.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.event.model.Event;

import static ru.practicum.category.dto.CategoryMapper.toCategoryDto;
import static ru.practicum.user.dto.UserMapper.toUserShortDto;

@UtilityClass
public class EventMapper {
    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
            .id(event.getId())
            .title(event.getTitle())
            .annotation(event.getAnnotation())
            .description(event.getDescription())
            .createdOn(event.getCreatedOn())
            .eventDate(event.getEventDate())
            .publishedOn(event.getPublishedOn())
            .category(toCategoryDto(event.getCategory()))
            .location(event.getLocation())
            .initiator(toUserShortDto(event.getInitiator()))
            .paid(event.getPaid())
            .participantLimit(event.getParticipantLimit())
            .requestModeration(event.getRequestModeration())
            .state(event.getState())
            .build();
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
            .id(event.getId())
            .title(event.getTitle())
            .annotation(event.getAnnotation())
            .eventDate(event.getEventDate())
            .category(toCategoryDto(event.getCategory()))
            .initiator(toUserShortDto(event.getInitiator()))
            .paid(event.getPaid())
            .build();
    }

    public static Event toEvent(NewEventDto dto) {
        return Event.builder()
            .title(dto.getTitle())
            .annotation(dto.getAnnotation())
            .eventDate(dto.getEventDate())
            .description(dto.getDescription())
            .location(dto.getLocation())
            .paid(dto.getPaid())
            .participantLimit(dto.getParticipantLimit())
            .requestModeration(dto.getRequestModeration())
            .build();
    }
}