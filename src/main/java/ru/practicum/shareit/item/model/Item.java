package ru.practicum.shareit.item.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "items")
public class Item {
    @Id
    @Column(name = "item_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;
    @Column(name = "item_name", nullable = false)
    private String name;
    @Column(length = 1024)
    private String description;
    @Column(nullable = false)
    private Boolean available;

    public Item(Long id, Long ownerId, String name, String description, Boolean available) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public Item() {

    }
}
