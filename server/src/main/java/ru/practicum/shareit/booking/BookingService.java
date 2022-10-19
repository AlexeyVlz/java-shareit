package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
import ru.practicum.shareit.user.model.User;

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
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new DataNotFound(
                String.format("Вещь с id %d в базе данных не обнаружен", bookingDto.getItemId())));
        if (!item.getAvailable()) {
            throw new ErrorArgumentException("Бронирование данной вещи невозможно, статус вещи 'занята'");
        }
        LocalDateTime timeNow = LocalDateTime.now();
        if (bookingDto.getStart().isBefore(timeNow)
                || bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ErrorArgumentException("Некорректно указаны сроки бронирования");
        }
        if (item.getOwner().getId().equals(bookerId)) {
            throw new ValidationDataException("Владелец не может бранировать свою вещь");
        }
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new DataNotFound(
                String.format("Пользователь с id %d в базе данных не обнаружен", bookerId)));
        Booking booking = mapper.toBooking(bookingDto, item, booker);
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
        Booking booking1 = bookingRepository.save(booking);
        return BookingMapper.toInfoBookingDto(booking1);
    }

    public InfoBookingDto getBookingById(Long bookingId, Long userId) {
        userValidation(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new DataNotFound(
                String.format("Бронирование с id %d в базе данных не обнаружено", bookingId)));
        if (!userId.equals(booking.getItem().getOwner().getId()) && !userId.equals(booking.getBooker().getId())) {
            throw new ValidationDataException("Данные по бронированию может запросить только владалей вещи, " +
                    "либо пользователь создавший бронь");
        }
        return BookingMapper.toInfoBookingDto(booking);
    }

    public List<InfoBookingDto> getBookingsByUserId(Long userId, String state, PageRequest pageRequest) {
        userValidation(userId);
        try {
            State.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ErrorArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
        return setBookingStatus(bookingRepository.findBookingsByBookerId(userId, pageRequest), state).stream()
                .map(BookingMapper::toInfoBookingDto).collect(Collectors.toList());
    }

    public List<InfoBookingDto> getBookingsByOwnerId(Long userId, String state, PageRequest pageRequest) {
        userValidation(userId);
        try {
            State.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ErrorArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
        return setBookingStatus(bookingRepository.findBookingsByItemOwnerId(userId, pageRequest), state).stream()
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
                    .filter((b) -> b.getState().equals(State.APPROVED))
                    .collect(Collectors.toList());
        } else if (State.valueOf(state.toUpperCase()).equals(State.CURRENT)) {
            return bookings.stream().filter((b) -> LocalDateTime.now().isAfter(b.getStart())
                    && LocalDateTime.now().isBefore(b.getEnd()))
                    .collect(Collectors.toList());
        }
        return bookings;
    }

    private void userValidation(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new DataNotFound(
                String.format("Пользователь с id %d в базе не обнаружен", userId)));
    }
}
