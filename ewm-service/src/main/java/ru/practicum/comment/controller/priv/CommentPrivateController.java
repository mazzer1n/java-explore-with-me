package ru.practicum.comment.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.service.CommentPrivateServiceInterface;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentPrivateController {
    private final CommentPrivateServiceInterface commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto save(@PathVariable Long userId, @PathVariable Long eventId,
        @RequestBody @Valid NewCommentDto dto) {
        log.info("Добавление нового комментария");

        return commentService.save(userId, eventId, dto);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto update(@PathVariable Long userId, @PathVariable Long commentId,
        @RequestBody @Valid NewCommentDto dto) {
        log.info("Редактирование комментария");

        return commentService.update(userId, commentId, dto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("Удаление комментария");

        commentService.delete(userId, commentId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> findAllByAuthorId(
        @PathVariable Long userId,
        @PositiveOrZero @RequestParam(defaultValue = "0", required = false) Integer from,
        @Positive @RequestParam(defaultValue = "10", required = false) Integer size) {
        log.info("Получение комментариев пользователя с id " + userId);

        return commentService.findAllByAuthorId(userId, from, size);
    }
}