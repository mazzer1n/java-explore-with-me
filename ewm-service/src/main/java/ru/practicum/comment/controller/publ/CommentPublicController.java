package ru.practicum.comment.controller.publ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentPublicServiceInterface;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentPublicController {
    private final CommentPublicServiceInterface commentService;

    @GetMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> findAll(
        @PathVariable Long eventId,
        @PositiveOrZero @RequestParam(defaultValue = "0", required = false) Integer from,
        @Positive @RequestParam(defaultValue = "10", required = false) Integer size) {
        log.info("Получение комментариев события с id " + eventId);

        return commentService.findAllByEventId(eventId, from, size);
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto findById(@PathVariable Long commentId) {
        log.info("Получение комментария с id " + commentId);

        return commentService.findById(commentId);
    }
}