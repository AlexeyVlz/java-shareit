package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.Positive;

@Data
public class ItemDto {

    @Positive
    private Long id;
    private String name;
    private String description;
    private Boolean available;

    public ItemDto(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
