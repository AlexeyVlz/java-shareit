package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.exception.DataNotFound;
import ru.practicum.shareit.exception.ErrorArgumentException;
import ru.practicum.shareit.exception.ValidationDataException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper mapper;
    private final CommentMapper commentMapper;

    @Autowired
    public ItemService(ItemRepository itemRepository, ItemMapper mapper, UserRepository userRepository,
                       BookingRepository bookingRepository, CommentRepository commentRepository,
                       CommentMapper commentMapper) {
        this.itemRepository = itemRepository;
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    public InfoItemDto createItem(ItemDto itemDto, Long ownerId) {
        itemValidation(itemDto.getId());
        userValidation(ownerId);
        return mapper.toInfoItemDto(itemRepository.save(mapper.toItem(itemDto, ownerId)));
    }

    public InfoItemDto updateItem(ItemDto itemDto, Long ownerId) {
        Item item = itemValidation(itemDto.getId());
        userValidation(ownerId);
        return mapper.toInfoItemDto(itemRepository.save(updateItemFromRepository(itemDto, ownerId, item)));
    }

    public InfoItemDto getItemById(Long itemId, Long userId) {
        userValidation(userId);
        Item item = itemValidation(itemId);
        InfoItemDto infoItemDto;
        if (item.getOwner().getId().equals((userId))) {
            infoItemDto = mapper.toInfoItemDto(item);
        } else {
            infoItemDto = mapper.toInfoItemDtoNotOwner(item);
        }
        return infoItemDto;
    }

    public List<InfoItemDto> getAllItemsByOwnerId(Long ownerId) {
        userValidation(ownerId);
        return itemRepository.findByOwnerId(ownerId).stream().map(mapper::toInfoItemDto).collect(Collectors.toList());
    }

    public List<InfoItemDto> searchItems(String text) {
        return itemRepository.findByNameContainsOrDescriptionContainsIgnoreCase(text, text)
                .stream().filter((i) -> i.getAvailable()).map(mapper::toInfoItemDto).collect(Collectors.toList());
    }

    public InfoCommentDto createComment(Long itemId, Long userId, CommentDto commentDto) {
        itemValidation(itemId);
        userValidation(userId);
        List<Booking> bookingList = bookingRepository.findBookingsByBookerIdAndItemId(itemId, userId);
        bookingList.removeIf((b) -> b.getState().equals(State.REJECTED));
        bookingList.removeIf((b) -> b.getEnd().isAfter(LocalDateTime.now()));
        if (bookingList.size() == 0) {
            throw new ErrorArgumentException("Оставить комментарий к вещи может только пользователь, " +
                    "бравший её в аренду");
        }
        return CommentMapper.toInfoCommentDto(commentRepository.save(commentMapper.toComment(itemId, userId, commentDto)));
    }

    private Item updateItemFromRepository(ItemDto itemDto, Long ownerId, Item item) {
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new ValidationDataException("Некорректно указан собственник вещи");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return item;
    }

    private Item itemValidation(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new DataNotFound(
                String.format("Вещи с id %d в базе данных не обнаружен", itemId)));
    }

    private void userValidation(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new DataNotFound(
                String.format("Пользователь с id %d в базе данных не обнаружен", userId)));
    }
}

