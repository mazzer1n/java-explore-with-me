package ru.practicum.exception.exceptions;

public class CommentConflictException extends ConflictException {
    public CommentConflictException(String message) {
        super(message);
    }
}