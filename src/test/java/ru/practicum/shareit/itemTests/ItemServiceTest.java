package ru.practicum.shareit.itemTests;

import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.ObjectsForTests;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.DataNotFound;
import ru.practicum.shareit.exception.ErrorArgumentException;
import ru.practicum.shareit.exception.ValidationDataException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemServiceTest {

    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    ItemMapper mapper;
    CommentMapper commentMapper;
    ItemService itemService;

    User user1 = ObjectsForTests.getUser1();
    UserDto userError = ObjectsForTests.getUserDtoError();
    ItemDto itemDto1 = ObjectsForTests.getItemDto1();
    Item item1 = ObjectsForTests.getItem1();
    InfoItemDto infoItemDto1 = ObjectsForTests.getInfoItemDto1();
    InfoItemDto infoItemDtoToOwner = ObjectsForTests.ItemDtoToOwner();
    Booking futureBooking = ObjectsForTests.futureBooking();
    Booking pastBooking = ObjectsForTests.pastBooking();
    Booking rejectedBooking = ObjectsForTests.rejectedBooking();
    CommentDto commentDto = ObjectsForTests.commentDto();


    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        mapper = mock(ItemMapper.class);
        commentMapper = mock(CommentMapper.class);
        itemService = new ItemService(itemRepository, mapper, userRepository, bookingRepository,
                commentRepository, commentMapper);
    }

    @Test
    void createItem() {
        userValidation();
        when(mapper.toItem(itemDto1, 1L)).thenReturn(item1);
        when(mapper.toInfoItemDto(item1)).thenReturn(infoItemDto1);
        when(itemRepository.save(item1)).thenReturn(item1);
        Assertions.assertEquals(infoItemDto1, itemService.createItem(itemDto1, user1.getId()));
    }

    @Test
    void updateItem () {
        userValidation();
        itemValidation();
        when(mapper.toInfoItemDto(item1)).thenReturn(infoItemDto1);
        when(itemRepository.save(item1)).thenReturn(item1);
        Assertions.assertEquals(itemService.updateItem(itemDto1, 1L), infoItemDto1);
        ValidationDataException exception = Assertions.assertThrows(
                ValidationDataException.class,
                () -> itemService.updateItem(itemDto1, 777L));
        Assertions.assertEquals("Некорректно указан собственник вещи", exception.getMessage());
    }

    @Test
    void getItemById () {
        userValidation();
        itemValidation();
        when(mapper.toInfoItemDto(any())).thenReturn(infoItemDto1);
        when(mapper.toInfoItemDtoNotOwner(any())).thenReturn(infoItemDtoToOwner);
        Assertions.assertEquals(itemService.getItemById(1L, 1L), infoItemDtoToOwner);
        Assertions.assertEquals(itemService.getItemById(1L, 333L), infoItemDto1);
    }

    @Test
    void getAllItemsByOwnerId () {
        userValidation();
        when(itemRepository.findByOwnerId(any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(item1)));
        when(mapper.toInfoItemDto(any())).thenReturn(infoItemDto1);
        Assertions.assertEquals(new ArrayList<>(List.of(infoItemDto1)),
                itemService.getAllItemsByOwnerId(1L, PageRequest.of(0, 10)));
    }

    @Test
    void searchItems () {
        when(itemRepository.findByNameContainsOrDescriptionContainsIgnoreCase(any(), any(), any()))
                .thenReturn(new ArrayList<>(Collections.singletonList(item1)));
        when(mapper.toInfoItemDto(any())).thenReturn(infoItemDto1);
        Assertions.assertEquals(new ArrayList<>(List.of(infoItemDto1)),
                itemService.searchItems("text", PageRequest.of(0, 10)));
    }

    @Test
    void createComment () {
        userValidation();
        itemValidation();
        when(bookingRepository.findBookingsByBookerIdAndItemId(3L, 3L))
                .thenReturn(new ArrayList<>(Arrays.asList(futureBooking, rejectedBooking)));
        ErrorArgumentException exception = Assertions.assertThrows(
                ErrorArgumentException.class,
                () -> itemService.createComment(3L, 3L, commentDto));
        Assertions.assertEquals("Оставить комментарий к вещи может только пользователь, бравший её в аренду",
                exception.getMessage());

        when(bookingRepository.findBookingsByBookerIdAndItemId(1L, 3L))
                .thenReturn(new ArrayList<>(Arrays.asList(futureBooking, pastBooking, rejectedBooking)));
        when(commentMapper.toComment(anyLong(), anyLong(), any()))
                .thenReturn(ObjectsForTests.comment());
        when(commentRepository.save(any()))
                .thenReturn(ObjectsForTests.comment());
        Assertions.assertEquals(itemService.createComment(1L, 3L, commentDto),
                ObjectsForTests.infoCommentDto());
    }

        void userValidation() {
            when(userRepository.findById(anyLong()))
                    .thenAnswer(invocationOnMock -> {
                        Long userId = invocationOnMock.getArgument(0, Long.class);
                        if (userId == 777L) {
                            throw new DataNotFound(
                                    String.format("Пользователь с id %d в базе данных не обнаружен", 777));
                        } else {
                            return Optional.of(user1);
                        }
                    });
            DataNotFound exception = Assertions.assertThrows(
                    DataNotFound.class,
                    () -> itemService.createItem(itemDto1, userError.getId()));
            Assertions.assertEquals("Пользователь с id 777 в базе данных не обнаружен",
                    exception.getMessage());
        }

        void itemValidation() {
         when(itemRepository.findById(anyLong()))
                 .thenAnswer(invocationOnMock -> {
                     Long id = invocationOnMock.getArgument(0, Long.class);
                     if (id == 777){
                         throw new DataNotFound(
                                 String.format("Вещи с id %d в базе данных не обнаружен", 777));
                     }
                     else {
                         return Optional.of(item1);
                     }
                 });
        }
    }