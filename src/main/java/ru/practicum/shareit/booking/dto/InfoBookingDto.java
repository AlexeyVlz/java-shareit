package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class InfoBookingDto {

    private Long id;
    private Long bookerId;
    private Item item;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;

    public static class Item {
        Long id;
        String name;

        public Item(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
