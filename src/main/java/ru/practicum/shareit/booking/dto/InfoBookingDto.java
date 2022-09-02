package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class InfoBookingDto {

    private Long id;
    private User booker;
    private Item item;
    private LocalDateTime start;
    private LocalDateTime end;
    private State status;

    /*public static class Item {
        Long id;
        String name;

        public Item(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }*/
}
