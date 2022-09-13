package ru.practicum.shareit.requests.dto;

import lombok.Data;
import ru.practicum.shareit.responses.Response;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InfoItemRequestDto {

    private Long id;

    private String description;
    private LocalDateTime created;
    private List<Response> items;

    public InfoItemRequestDto(Long id, String description, LocalDateTime creationTime, List<Response> items) {
        this.id = id;
        this.description = description;
        this.created = creationTime;
        this.items = items;
    }
}
