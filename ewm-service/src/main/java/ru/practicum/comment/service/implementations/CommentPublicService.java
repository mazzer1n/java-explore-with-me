package ru.practicum.comment.service.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.comment.service.CommentPublicServiceInterface;
import ru.practicum.exception.exceptions.CommentNotFoundException;
import ru.practicum.event.service.implementations.EventPrivateService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.comment.dto.CommentMapper.toCommentDto;

@Service
public class CommentPublicService implements CommentPublicServiceInterface {
    private final CommentRepository commentRepository;
    private final EventPrivateService eventService;

    @Autowired
    public CommentPublicService(CommentRepository commentRepository, @Lazy EventPrivateService eventService) {
        this.commentRepository = commentRepository;
        this.eventService = eventService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findAllByEventId(Long eventId, int from, int size) {
        eventService.getExistingEvent(eventId);
        PageRequest page = PageRequest.of(from / size, size);

        return commentRepository.findAllByEventId(eventId, page).stream()
            .map(CommentMapper::toCommentDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto findById(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
            () -> new CommentNotFoundException("Комментарий с id " + commentId + " не найден.")
        );

        return toCommentDto(comment);
    }
}