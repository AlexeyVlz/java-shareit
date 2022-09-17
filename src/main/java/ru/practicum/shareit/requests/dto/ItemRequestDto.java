package ru.practicum.shareit.requests.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
public class ItemRequestDto {

    @NotBlank @NotNull
    private String description;
}
