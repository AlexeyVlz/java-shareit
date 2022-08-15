package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;

@Data
public class Item {

    Long itemId;
    Long userId;
    @NonNull @NotBlank
    String name;
    @NonNull @NotBlank
    String description;
    @NonNull
    Boolean available;
}
