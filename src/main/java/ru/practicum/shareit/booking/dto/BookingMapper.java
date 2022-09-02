package ru.practicum.shareit.booking.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exception.DataNotFound;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;


@Component
public class BookingMapper {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingMapper(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public Booking toBooking(BookingDto bookingDto, Long bookerId) {
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new DataNotFound(
                String.format("Вещь с id %d в базе данных не обнаружен", bookingDto.getItemId())));
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new DataNotFound(
                String.format("Пользователь с id %d в базе данных не обнаружен", bookerId)));
        return new Booking(booker, item, bookingDto.getStart(), bookingDto.getEnd());
    }

    public static InfoBookingDto toInfoBookingDto(Booking booking) {
        //InfoBookingDto.Item item = new InfoBookingDto.Item(booking.getItem().getId(), booking.getItem().getName());
        return new InfoBookingDto(booking.getId(),
                                    booking.getBooker(),
                                    booking.getItem(),
                                    booking.getStart(),
                                    booking.getEnd(),
                                    booking.getState());
    }
}
