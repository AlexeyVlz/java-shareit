package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
public class Item {
    @Positive
    Long id;
    @Positive
    private Long ownerId;
    private String name;
    private String description;
    private Boolean available;

    public Item(Long id, Long ownerId, String name, String description, Boolean available) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
