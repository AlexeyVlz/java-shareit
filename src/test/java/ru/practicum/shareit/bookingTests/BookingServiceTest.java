package ru.practicum.shareit.bookingTests;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.ObjectsForTests;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.DataNotFound;
import ru.practicum.shareit.exception.ErrorArgumentException;
import ru.practicum.shareit.exception.ValidationDataException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Slf4j
class BookingServiceTest {

    BookingRepository bookingRepository;
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingMapper mapper;
    BookingService bookingService;

    @BeforeEach
    void beforeEach() {
        bookingRepository = mock(BookingRepository.class);
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        mapper = mock(BookingMapper.class);
        bookingService = new BookingService(bookingRepository, mapper, itemRepository, userRepository);
    }

    @Test
    void createBooking() {
        BookingDto bookingDto = ObjectsForTests.futureBookingDto1();
        bookingDto.setItemId(777L);
        Item item = ObjectsForTests.getItem3();
        item.setAvailable(false);
        when(itemRepository.findById(any()))
                .thenAnswer(invocationOnMock -> {
                    Long itemId = invocationOnMock.getArgument(0, Long.class);
                    if (itemId == 777) {
                        throw new DataNotFound("Вещь с id 777 в базе данных не обнаружен");
                    } else {
                        return Optional.of(item);
                    }
                });
        DataNotFound exception = Assertions.assertThrows(
                DataNotFound.class,
                () -> bookingService.createBooking(bookingDto, 1L));
        Assertions.assertEquals("Вещь с id 777 в базе данных не обнаружен",
                exception.getMessage());

        bookingDto.setItemId(3L);
        ErrorArgumentException exception1 = Assertions.assertThrows(
                ErrorArgumentException.class,
                () -> bookingService.createBooking(bookingDto, 1L));
        Assertions.assertEquals("Бронирование данной вещи невозможно, статус вещи 'занята'",
                exception1.getMessage());

        item.setAvailable(true);
        bookingDto.setStart(LocalDateTime.now().minus(Period.ofDays(1)));
        ErrorArgumentException exception2 = Assertions.assertThrows(
                ErrorArgumentException.class,
                () -> bookingService.createBooking(bookingDto, 1L));
        Assertions.assertEquals("Некорректно указаны сроки бронирования",
                exception2.getMessage());

        bookingDto.setStart(LocalDateTime.of(2023, 10, 1, 12, 0));
        bookingDto.setEnd(LocalDateTime.of(2023, 10, 1, 11, 0));
        ErrorArgumentException exception3 = Assertions.assertThrows(
                ErrorArgumentException.class,
                () -> bookingService.createBooking(bookingDto, 1L));
        Assertions.assertEquals("Некорректно указаны сроки бронирования",
                exception3.getMessage());

        bookingDto.setEnd(LocalDateTime.of(2023, 10, 2, 12, 0));
        ValidationDataException exception4 = Assertions.assertThrows(
                ValidationDataException.class,
                () -> bookingService.createBooking(bookingDto, 2L));
        Assertions.assertEquals("Владелец не может бранировать свою вещь",
                exception4.getMessage());

        userValidation();
        DataNotFound exception5 = Assertions.assertThrows(
                DataNotFound.class,
                () -> bookingService.getBookingById(1L, 777L));
        Assertions.assertEquals("Пользователь с id 777 в базе данных не обнаружен",
                exception5.getMessage());

        User booker = ObjectsForTests.getUser1();
        Booking booking = ObjectsForTests.futureBooking();
        when(mapper.toBooking(bookingDto, item, booker))
                        .thenReturn(booking);
        when(bookingRepository.save(booking))
                .thenReturn(booking);

        Assertions.assertEquals(bookingService.createBooking(bookingDto, 1L),
                ObjectsForTests.waitingFutureInfoBookingDto1());
    }

    @Test
    void approveBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(ObjectsForTests.futureBooking()));
        Booking approvedBooking = ObjectsForTests.futureBooking();
        approvedBooking.setState(State.APPROVED);
        when(bookingRepository.save(any())).thenReturn(approvedBooking);
        Assertions.assertEquals(ObjectsForTests.approvedFutureInfoBookingDto1(),
                bookingService.approveBooking(1L, true, 2L));

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(ObjectsForTests.futureBooking()));
        Booking rejectedBooking = ObjectsForTests.futureBooking();
        rejectedBooking.setState(State.REJECTED);
        when(bookingRepository.save(any())).thenReturn(rejectedBooking);
        Assertions.assertEquals(ObjectsForTests.rejectedFutureInfoBookingDto1(),
                bookingService.approveBooking(1L, false, 2L));

        ValidationDataException exception = Assertions.assertThrows(
                ValidationDataException.class,
                () -> bookingService.approveBooking(1L, true, 777L));
        Assertions.assertEquals("Подтвердить запрос может только владелей вещи",
                exception.getMessage());

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(ObjectsForTests.pastBooking()));
        ErrorArgumentException exception1 = Assertions.assertThrows(
                ErrorArgumentException.class,
                () -> bookingService.approveBooking(1L, true, 2L));
        Assertions.assertEquals("Изменить статус подтвержденной брони невозможно",
                exception1.getMessage());
    }

    @Test
    void getBookingById() {
        userValidation();
        DataNotFound exception = Assertions.assertThrows(
                DataNotFound.class,
                () -> bookingService.getBookingById(1L, 777L));
        Assertions.assertEquals("Пользователь с id 777 в базе данных не обнаружен",
                exception.getMessage());

        when(bookingRepository.findById(anyLong()))
                .thenAnswer(invocationOnMock -> {
                    Long bookingId = invocationOnMock.getArgument(0, Long.class);
                    if (bookingId == 777L) {
                        throw new DataNotFound(
                                String.format("Бронирование с id %d в базе данных не обнаружено", 777));
                    } else {
                        return Optional.of(ObjectsForTests.futureBooking());
                    }
                });
        DataNotFound exception1 = Assertions.assertThrows(
                DataNotFound.class,
                () -> bookingService.getBookingById(777L, 1L));
        Assertions.assertEquals("Бронирование с id 777 в базе данных не обнаружено",
                exception1.getMessage());

        ValidationDataException exception2 = Assertions.assertThrows(
                ValidationDataException.class,
                () -> bookingService.getBookingById(1L, 333L));
        Assertions.assertEquals("Данные по бронированию может запросить только владалей вещи, " +
                        "либо пользователь создавший бронь",
                exception2.getMessage());

        Assertions.assertEquals(bookingService.getBookingById(1L, 1L),
                ObjectsForTests.futureInfoBookingDto1());
    }

    @Test
    void getBookingsByUserId() {
        userValidation();
        DataNotFound exception = Assertions.assertThrows(
                DataNotFound.class,
                () -> bookingService.getBookingsByUserId(777L, "APPROVED", PageRequest.of(0, 10)));
        Assertions.assertEquals("Пользователь с id 777 в базе данных не обнаружен",
                exception.getMessage());

        ErrorArgumentException exception1 = Assertions.assertThrows(
                ErrorArgumentException.class,
                () -> bookingService.getBookingsByUserId(1L, "None", PageRequest.of(0, 10)));
        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS",
                exception1.getMessage());

        when(bookingRepository.findBookingsByBookerId(anyLong(), any()))
                .thenReturn(ObjectsForTests.bookingsForSetStatus());
        Assertions.assertEquals(bookingService.getBookingsByUserId(1L, "WAITING", PageRequest.of(0, 10)),
                new ArrayList<>(List.of(ObjectsForTests.waitingInfoBookingDTO())));
        Assertions.assertEquals(bookingService.getBookingsByUserId(1L, "REJECTED", PageRequest.of(0, 10)),
                new ArrayList<>(List.of(ObjectsForTests.rejectedInfoBookingDTO())));
        Assertions.assertEquals(bookingService.getBookingsByUserId(1L, "FUTURE", PageRequest.of(0, 10)),
                new ArrayList<>(Arrays.asList(ObjectsForTests.waitingInfoBookingDTO(), ObjectsForTests.futureInfoBookingDTO())));
        Assertions.assertEquals(bookingService.getBookingsByUserId(1L, "PAST", PageRequest.of(0, 10)),
                new ArrayList<>(List.of(ObjectsForTests.pastInfoBookingDTO())));
        Assertions.assertEquals(bookingService.getBookingsByUserId(1L, "CURRENT", PageRequest.of(0, 10)),
                new ArrayList<>(List.of(ObjectsForTests.currentInfoBookingDTO())));
    }

    @Test
    void getBookingsByOwnerId() {
        userValidation();
        DataNotFound exception = Assertions.assertThrows(
                DataNotFound.class,
                () -> bookingService.getBookingsByOwnerId(777L, "APPROVED", PageRequest.of(0, 10)));
        Assertions.assertEquals("Пользователь с id 777 в базе данных не обнаружен",
                exception.getMessage());

        ErrorArgumentException exception1 = Assertions.assertThrows(
                ErrorArgumentException.class,
                () -> bookingService.getBookingsByOwnerId(1L, "None", PageRequest.of(0, 10)));
        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS",
                exception1.getMessage());
        when(bookingRepository.findBookingsByItemOwnerId(anyLong(), any()))
                .thenReturn(ObjectsForTests.bookingsForSetStatus());
        Assertions.assertEquals(bookingService.getBookingsByOwnerId(1L, "WAITING", PageRequest.of(0, 10)),
                new ArrayList<>(List.of(ObjectsForTests.waitingInfoBookingDTO())));
        Assertions.assertEquals(bookingService.getBookingsByOwnerId(1L, "REJECTED", PageRequest.of(0, 10)),
                new ArrayList<>(List.of(ObjectsForTests.rejectedInfoBookingDTO())));
        Assertions.assertEquals(bookingService.getBookingsByOwnerId(1L, "FUTURE", PageRequest.of(0, 10)),
                new ArrayList<>(Arrays.asList(ObjectsForTests.waitingInfoBookingDTO(), ObjectsForTests.futureInfoBookingDTO())));
        Assertions.assertEquals(bookingService.getBookingsByOwnerId(1L, "PAST", PageRequest.of(0, 10)),
                new ArrayList<>(List.of(ObjectsForTests.pastInfoBookingDTO())));
        Assertions.assertEquals(bookingService.getBookingsByOwnerId(1L, "CURRENT", PageRequest.of(0, 10)),
                new ArrayList<>(List.of(ObjectsForTests.currentInfoBookingDTO())));
    }

    void userValidation() {
        when(userRepository.findById(anyLong()))
                .thenAnswer(invocationOnMock -> {
                    Long userId = invocationOnMock.getArgument(0, Long.class);
                    if (userId == 777L) {
                        throw new DataNotFound(
                                String.format("Пользователь с id %d в базе данных не обнаружен", 777));
                    } else {
                        return Optional.of(ObjectsForTests.getUser1());
                    }
                });
    }
}