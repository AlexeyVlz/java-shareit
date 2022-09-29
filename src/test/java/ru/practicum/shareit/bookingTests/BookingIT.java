package ru.practicum.shareit.bookingTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.ObjectsForTests;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringJUnitConfig({ShareItApp.class, ItemService.class, UserServiceImpl.class, BookingService.class})
public class BookingIT {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @Test
    void createBooking() {
        UserDto userDto1 = ObjectsForTests.getUserDto1();
        userService.createUser(userDto1);
        UserDto userDto2 = ObjectsForTests.getUserDto2();
        userService.createUser(userDto2);
        ItemDto itemDto = ObjectsForTests.getItemDto3();
        itemDto.setRequestId(null);
        itemService.createItem(itemDto, 1L);
        itemService.createItem(itemDto, 1L);
        itemService.createItem(itemDto, 2L);
        BookingDto bookingDto = ObjectsForTests.futureBookingDto1();
        bookingService.createBooking(bookingDto, 1L);
        Booking booking = ObjectsForTests.futureBooking();

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking queryBooking = query
                .setParameter("id", 1L)
                .getSingleResult();
        Assertions.assertEquals(booking, queryBooking);
    }
}
