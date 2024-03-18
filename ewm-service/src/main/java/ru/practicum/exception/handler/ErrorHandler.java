package ru.practicum.exception.handler;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.exception.exceptions.BadRequestException;
import ru.practicum.exception.exceptions.ConflictException;
import ru.practicum.exception.exceptions.NotFoundException;
import ru.practicum.exception.handler.ApiError;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException exception) {
        return new ApiError(
            List.of(Arrays.toString(exception.getStackTrace())),
            exception.getMessage(),
            exception.getLocalizedMessage(),
            HttpStatus.NOT_FOUND.toString(),
            LocalDateTime.now()
        );
    }

    @ExceptionHandler({BadRequestException.class, NumberFormatException.class,
        MethodArgumentTypeMismatchException.class, MethodArgumentNotValidException.class,
        MissingServletRequestParameterException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(final BadRequestException exception) {
        return new ApiError(
            List.of(Arrays.toString(exception.getStackTrace())),
            exception.getMessage(),
            exception.getLocalizedMessage(),
            HttpStatus.BAD_REQUEST.toString(),
            LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException exception) {
        return new ApiError(
            List.of(Arrays.toString(exception.getStackTrace())),
            exception.getMessage(),
            exception.getLocalizedMessage(),
            HttpStatus.CONFLICT.toString(),
            LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleValidationException(final DataIntegrityViolationException exception) {
        return new ApiError(
            List.of(Arrays.toString(exception.getStackTrace())),
            exception.getMessage(),
            exception.getLocalizedMessage(),
            HttpStatus.CONFLICT.toString(),
            LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleStatusException(final Exception exception) {
        return new ApiError(
            List.of(Arrays.toString(exception.getStackTrace())),
            exception.getMessage(),
            exception.getLocalizedMessage(),
            HttpStatus.CONFLICT.toString(),
            LocalDateTime.now()
        );
    }
}