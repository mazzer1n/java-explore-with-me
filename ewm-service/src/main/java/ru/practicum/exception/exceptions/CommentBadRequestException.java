package ru.practicum.exception.exceptions;

public class CommentBadRequestException extends BadRequestException {
    public CommentBadRequestException(String message) {
        super(message);
    }
}