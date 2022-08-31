package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment toComment(Long itemId, Long userId, CommentDto commentDto) {
        return new Comment(commentDto.getText(), itemId, userId, LocalDateTime.now());
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getItemId(),
                comment.getAuthorId(),
                comment.getCreated());
    }
}
