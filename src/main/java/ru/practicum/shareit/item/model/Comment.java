package ru.practicum.shareit.item.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @Column(name = "comment_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "comment_text", nullable = false)
    String text;
    @Column(name = "item_id", nullable = false)
    Long itemId;
    @Column(name = "author_id", nullable = false)
    Long authorId;
    @Column(name = "created", nullable = false)
    LocalDateTime created;

    public Comment(String text, Long itemId, Long authorId, LocalDateTime created) {
        this.text = text;
        this.itemId = itemId;
        this.authorId = authorId;
        this.created = created;
    }
}
