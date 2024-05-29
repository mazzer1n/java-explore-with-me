package ru.practicum.comment.service.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentMapper;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.comment.service.CommentPrivateServiceInterface;
import ru.practicum.exception.exceptions.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.implementations.EventPrivateService;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.comment.dto.CommentMapper.toCommentDto;
import static ru.practicum.comment.dto.CommentMapper.toCommentFromNewCommentDto;

@Service
public class CommentPrivateService implements CommentPrivateServiceInterface {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final EventPrivateService eventService;

    @Autowired
    public CommentPrivateService(CommentRepository commentRepository,
        @Lazy UserService userService,
        @Lazy EventPrivateService eventService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.eventService = eventService;
    }

    @Transactional
    @Override
    public CommentDto save(Long userId, Long eventId, NewCommentDto dto) {
        User user = userService.getExistingUser(userId);
        Event event = eventService.getExistingEvent(eventId);
        eventService.validateEventToAddComment(event);

        Comment comment = toCommentFromNewCommentDto(dto);
        comment.setCreated(LocalDateTime.now());
        comment.setEvent(event);
        comment.setAuthor(user);

        return toCommentDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public CommentDto update(Long userId, Long commentId, NewCommentDto dto) {
        Comment updated = getExistingComment(commentId);
        validateAuthorOfComment(updated, userId);
        updated.setText(dto.getText());

        return toCommentDto(commentRepository.save(updated));
    }

    @Transactional
    @Override
    public void delete(Long userId, Long commentId) {
        Comment comment = getExistingComment(commentId);
        validateAuthorOfComment(comment, userId);

        commentRepository.deleteById(commentId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> findAllByAuthorId(Long userId, int from, int size) {
        userService.getExistingUser(userId);
        PageRequest page = PageRequest.of(from / size, size);

        return commentRepository.findAllByAuthorId(userId, page).stream()
            .map(CommentMapper::toCommentDto)
            .collect(Collectors.toList());
    }

    public Comment getExistingComment(long id) {
        return commentRepository.findById(id).orElseThrow(
            () -> new CommentNotFoundException("Комментарий с id " + id + " не найден.")
        );
    }

    private void validateAuthorOfComment(Comment comment, Long userId) {
        userService.getExistingUser(userId);
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new CommentBadRequestException("Комментарий может изменить только его создатель");
        }
    }
}