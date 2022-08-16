package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NonNull;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
public class ItemDto {

    @Positive
    Long id;
    @NotBlank
    String name;
    String description;
    Boolean available;

    public ItemDto(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
