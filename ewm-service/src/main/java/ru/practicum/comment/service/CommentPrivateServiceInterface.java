package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentPrivateServiceInterface {
    CommentDto save(Long userId, Long eventId, NewCommentDto dto);

    CommentDto update(Long userId, Long commentId, NewCommentDto dto);

    void delete(Long userId, Long commentId);

    List<CommentDto> findAllByAuthorId(Long userId, int from, int size);
}