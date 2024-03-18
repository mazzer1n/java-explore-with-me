package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;

import java.util.List;

public interface CommentPublicServiceInterface {
    List<CommentDto> findAllByEventId(Long eventId, int from, int size);

    CommentDto findById(Long commentId);
}