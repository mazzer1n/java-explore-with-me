package ru.practicum.exception.exceptions;

public class RequestConflictException extends ConflictException {
    public RequestConflictException(String message) {
        super(message);
    }
}