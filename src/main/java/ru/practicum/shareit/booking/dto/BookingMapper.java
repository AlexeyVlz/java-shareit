package ru.practicum.shareit.booking.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.DataNotFound;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class BookingMapper {

    private final ItemRepository repository;

    @Autowired
    public BookingMapper(ItemRepository repository) {
        this.repository = repository;
    }

    public Booking toBooking(BookingDto bookingDto, Long bookerId) {
        Item item = repository.findById(bookingDto.getItemId()).orElseThrow(() -> new DataNotFound(
                String.format("Вещь с id %d в базе данных не обнаружен", bookingDto.getItemId())));
        return new Booking(bookerId, item, bookingDto.getStart(), bookingDto.getEnd());
    }

    public static InfoBookingDto toInfoBookingDto(Booking booking) {
        InfoBookingDto.Item item = new InfoBookingDto.Item(booking.getItem().getId(), booking.getItem().getName());
        InfoBookingDto infoBookingDto = new InfoBookingDto(booking.getId(),
                                                            booking.getBookerId(),
                                                            item,
                                                            booking.getStart(),
                                                            booking.getEnd(),
                                                            booking.getStatus());
        return BookingMapper.setStatus(infoBookingDto);
    }

    private static InfoBookingDto setStatus(InfoBookingDto infoBookingDto) {
        if (infoBookingDto.getStatus().equals(Status.APPROVED)) {
            if (infoBookingDto.getStart().isAfter(LocalDateTime.now())) {
                infoBookingDto.setStatus(Status.FUTURE);
            } else if (infoBookingDto.getEnd().isBefore(LocalDateTime.now())) {
                infoBookingDto.setStatus(Status.PAST);
            } else if (Instant.now().isAfter(infoBookingDto.getStart().toInstant(ZoneOffset.UTC))
                    && Instant.now().isBefore(infoBookingDto.getEnd().toInstant(ZoneOffset.UTC))) {
                infoBookingDto.setStatus(Status.CURRENT);
            }
        }
        return infoBookingDto;
    }
}
