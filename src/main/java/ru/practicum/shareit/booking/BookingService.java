package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.InfoBookingDto;
import ru.practicum.shareit.exception.DataNotFound;
import ru.practicum.shareit.exception.ValidationDataException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository repository;
    private final BookingMapper mapper;

    @Autowired
    public BookingService(BookingRepository repository, BookingMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public InfoBookingDto createBooking(BookingDto bookingDto, Long bookerId) {
        Booking booking = mapper.toBooking(bookingDto, bookerId);
        booking.setStatus(Status.WAITING);
        return BookingMapper.toInfoBookingDto(repository.save(booking));
    }

    public InfoBookingDto approveBooking(Long bookingId, Boolean approved, Long ownerId) {
        Booking booking = repository.findById(bookingId).orElseThrow(() -> new DataNotFound(
                String.format("Бронирование с id %d в базе данных не обнаружен", bookingId)));
        if (!ownerId.equals(booking.getItem().getOwner().getId())) {
            throw new ValidationDataException("Подтвердить запрос может только владелей вещи");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toInfoBookingDto(booking);
    }

    public InfoBookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = repository.findById(bookingId).orElseThrow(() -> new DataNotFound(
                String.format("Бронирование с id %d в базе данных не обнаружен", bookingId)));
        if (!userId.equals(booking.getItem().getOwner().getId()) && !userId.equals(booking.getBookerId())) {
            throw new ValidationDataException("Данные по бронированию может запросить только владалей вещи, " +
                    "либо пользователь создавший бронь");
        }
        return BookingMapper.toInfoBookingDto(booking);
    }

    public List<InfoBookingDto> getBookingsByUserId(Long userId, String state) {
        if (Status.valueOf(state).equals(Status.CURRENT)) {
            return repository.getCurrentBookingByBooker(LocalDateTime.now(), userId).stream()
                    .map(BookingMapper::toInfoBookingDto).collect(Collectors.toList());
        } else if (Status.valueOf(state).equals(Status.PAST)) {
            return repository.getPastBookingByBooker(LocalDateTime.now(), userId).stream()
                    .map(BookingMapper::toInfoBookingDto).collect(Collectors.toList());
        } else if (Status.valueOf(state).equals(Status.FUTURE)) {
            return repository.getFutureBookingByBooker(LocalDateTime.now(), userId).stream()
                    .map(BookingMapper::toInfoBookingDto).collect(Collectors.toList());
        } else if (Status.valueOf(state).equals(Status.WAITING)) {
            return repository.getWaitingBookingByBooker(LocalDateTime.now(), userId).stream()
                    .map(BookingMapper::toInfoBookingDto).collect(Collectors.toList());
        } else if (Status.valueOf(state).equals(Status.REJECTED)) {
            return repository.getRejectedBookingByBooker(userId).stream()
                    .map(BookingMapper::toInfoBookingDto).collect(Collectors.toList());
        } else {
            return repository.findBookingsByBooker(userId).stream()
                    .map(BookingMapper::toInfoBookingDto).collect(Collectors.toList());
        }
    }

    public List<InfoBookingDto> getBookingsByOwnerId(Long userId, String state) {
        if (Status.valueOf(state).equals(Status.CURRENT)) {
            return repository.getCurrentBookingByOwner(LocalDateTime.now(), userId).stream()
                    .map(BookingMapper::toInfoBookingDto).collect(Collectors.toList());
        } else if (Status.valueOf(state).equals(Status.PAST)) {
            return repository.getPastBookingByOwner(LocalDateTime.now(), userId).stream()
                    .map(BookingMapper::toInfoBookingDto).collect(Collectors.toList());
        } else if (Status.valueOf(state).equals(Status.FUTURE)) {
            return repository.getFutureBookingByOwner(LocalDateTime.now(), userId).stream()
                    .map(BookingMapper::toInfoBookingDto).collect(Collectors.toList());
        } else if (Status.valueOf(state).equals(Status.WAITING)) {
            return repository.getWaitingBookingByOwner(LocalDateTime.now(), userId).stream()
                    .map(BookingMapper::toInfoBookingDto).collect(Collectors.toList());
        } else if (Status.valueOf(state).equals(Status.REJECTED)) {
            return repository.getRejectedBookingByOwner(userId).stream()
                    .map(BookingMapper::toInfoBookingDto).collect(Collectors.toList());
        } else {
            return repository.findBookingsByOwner(userId).stream()
                    .map(BookingMapper::toInfoBookingDto).collect(Collectors.toList());
        }
    }
}
