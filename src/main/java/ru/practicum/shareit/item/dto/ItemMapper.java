package ru.practicum.shareit.item.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.DataNotFound;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class ItemMapper {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemMapper(UserRepository userRepository, BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    public InfoItemDto toInfoItemDto(Item item) {
        if (item.getComments() == null) {
            item.setComments(new ArrayList<Comment>());
        }
        InfoItemDto infoItemDto = new InfoItemDto(item.getId(),
                item.getOwner(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getComments());
        List<Booking> bookingList = bookingRepository.findByItemId(infoItemDto.getId());
        infoItemDto.setLastBooking(InfoItemDto.toBookingDto(findLastBooking(bookingList)));
        infoItemDto.setNextBooking(InfoItemDto.toBookingDto(findNextBooking(bookingList)));
        return infoItemDto;
    }

    public InfoItemDto toInfoItemDtoNotOwner(Item item) {
        if (item.getComments() == null) {
            item.setComments(new ArrayList<Comment>());
        }
        InfoItemDto infoItemDto = new InfoItemDto(item.getId(),
                item.getOwner(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getComments());
        infoItemDto.setLastBooking(null);
        infoItemDto.setNextBooking(null);
        return infoItemDto;
    }


    public Item toItem(ItemDto itemDto, Long ownerId) {
        User user = userRepository.findById(ownerId).orElseThrow(() -> new DataNotFound(
                String.format("Пользователь с id %d в базе данных не обнаружен", ownerId)));
        return new Item(user, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
    }

    private Booking findLastBooking(List<Booking> bookingList) {
        return bookingList.stream()
                .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getEnd)).orElse(null);
    }

    private Booking findNextBooking(List<Booking> bookingList) {
        return bookingList.stream()
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart)).orElse(null);
    }
}
