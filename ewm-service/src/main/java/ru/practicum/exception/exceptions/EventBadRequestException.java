package ru.practicum.exception.exceptions;

public class EventBadRequestException extends BadRequestException {
    public EventBadRequestException(String message) {
        super(message);
    }
}