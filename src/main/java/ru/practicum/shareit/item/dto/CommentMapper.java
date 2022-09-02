package ru.practicum.shareit.item.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataNotFound;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class CommentMapper {

    private final UserRepository userRepository;

    @Autowired
    public CommentMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Comment toComment(Long itemId, Long userId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFound(
                String.format("Пользователь с id %d в базе данных не обнаружен", userId)));

        return new Comment(commentDto.getText(), itemId, user, LocalDateTime.now());
    }

    public static InfoCommentDto toInfoCommentDto(Comment comment) {
        return new InfoCommentDto(comment.getId(),
                comment.getText(),
                comment.getItemId(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }
}
