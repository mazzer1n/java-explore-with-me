package ru.practicum.comment.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.comment.model.Comment;

@UtilityClass
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
            .id(comment.getId())
            .text(comment.getText())
            .authorName(comment.getAuthor().getName())
            .created(comment.getCreated())
            .build();
    }

    public static Comment toCommentFromNewCommentDto(NewCommentDto dto) {
        return Comment.builder()
            .text(dto.getText())
            .build();
    }
}