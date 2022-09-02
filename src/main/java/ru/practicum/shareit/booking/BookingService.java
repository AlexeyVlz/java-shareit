package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.InfoBookingDto;
import ru.practicum.shareit.exception.DataNotFound;
import ru.practicum.shareit.exception.ErrorArgumentException;
import ru.practicum.shareit.exception.ValidationDataException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper mapper;

    @Autowired
    public BookingService(BookingRepository bookingRepository, BookingMapper mapper,
                          ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.mapper = mapper;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public InfoBookingDto createBooking(BookingDto bookingDto, Long bookerId) {
        bookingValidation(bookingDto, bookerId);
        Booking booking = mapper.toBooking(bookingDto, bookerId);
        booking.setState(State.WAITING);
        return BookingMapper.toInfoBookingDto(bookingRepository.save(booking));
    }

    public InfoBookingDto approveBooking(Long bookingId, Boolean approved, Long ownerId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new DataNotFound(
                String.format("Бронирование с id %d в базе данных не обнаружен", bookingId)));
        if (booking.getState().equals(State.APPROVED)) {
            throw new ErrorArgumentException("Изменить статус подтвержденной брони невозможно");
        }
        if (!ownerId.equals(booking.getItem().getOwner().getId())) {
            throw new ValidationDataException("Подтвердить запрос может только владелей вещи");
        }
        if (approved) {
            booking.setState(State.APPROVED);
        } else {
            booking.setState(State.REJECTED);
        }
        //booking.getItem().setAvailable(false);
        return BookingMapper.toInfoBookingDto(bookingRepository.save(booking));
    }

    public InfoBookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new DataNotFound(
                String.format("Бронирование с id %d в базе данных не обнаружен", bookingId)));
        if (!userId.equals(booking.getItem().getOwner().getId()) && !userId.equals(booking.getBooker().getId())) {
            throw new ValidationDataException("Данные по бронированию может запросить только владалей вещи, " +
                    "либо пользователь создавший бронь");
        }
        return BookingMapper.toInfoBookingDto(booking);
    }

    public List<InfoBookingDto> getBookingsByUserId(Long userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> new DataNotFound(
                String.format("Пользователь с id %d в базе не обнаружена", userId)));
        try {
            State.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ErrorArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
        return setBookingStatus(bookingRepository.findBookingsByBooker(userId), state).stream()
                .map(BookingMapper::toInfoBookingDto).collect(Collectors.toList());
    }

    public List<InfoBookingDto> getBookingsByOwnerId(Long userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> new DataNotFound(
                String.format("Пользователь с id %d в базе не обнаружена", userId)));
        try {
            State.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ErrorArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
        return setBookingStatus(bookingRepository.findBookingsByOwner(userId), state).stream()
                .map(BookingMapper::toInfoBookingDto).collect(Collectors.toList());
    }

    private List<Booking> setBookingStatus(List<Booking> bookings, String state) {
        if (State.valueOf(state.toUpperCase()).equals(State.WAITING)) {
            return bookings.stream().filter((b) -> b.getState().equals(State.WAITING))
                    .collect(Collectors.toList());
        } else if (State.valueOf(state.toUpperCase()).equals(State.REJECTED)) {
            return bookings.stream().filter((b) -> b.getState().equals(State.REJECTED))
                    .collect(Collectors.toList());
        } else if (State.valueOf(state.toUpperCase()).equals(State.FUTURE)) {
            return bookings.stream().filter((b) -> LocalDateTime.now().isBefore(b.getStart()))
                    .collect(Collectors.toList());
        } else if (State.valueOf(state.toUpperCase()).equals(State.PAST)) {
            return bookings.stream().filter((b) -> LocalDateTime.now().isAfter(b.getEnd()))
                    .collect(Collectors.toList());
        } else if (State.valueOf(state.toUpperCase()).equals(State.CURRENT)) {
            return bookings.stream().filter((b) -> LocalDateTime.now().isAfter(b.getStart())
                    && LocalDateTime.now().isBefore(b.getEnd())).collect(Collectors.toList());
        }
        return bookings;
    }


    private void bookingValidation(BookingDto bookingDto, Long bookerId) {
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new DataNotFound(
                String.format("Запрашиваемая вещь с id %d в базе не обнаружена", bookingDto.getItemId())));
        if (!item.getAvailable()) {
            throw new ErrorArgumentException("Бронирование данной вещи невозможно, статус вещи 'занята'");
        }
        LocalDateTime timeNow = LocalDateTime.now();
        if (bookingDto.getStart().isBefore(timeNow)
                || bookingDto.getEnd().isBefore(timeNow)
                || bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ErrorArgumentException("Некорректно указаны сроки бронирования");
        }
        userRepository.findById(bookerId).orElseThrow(() -> new DataNotFound(
                String.format("Пользователь с id %d в базе не обнаружена", bookerId)));
        if (item.getOwner().getId().equals(bookerId)) {
            throw new ValidationDataException("Владелец не может бранировать свою вещь");
        }
    }
}
