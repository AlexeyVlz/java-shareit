package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.DataNotFound;
import ru.practicum.shareit.exception.ValidationDataException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper mapper;

    @Autowired
    public ItemService(ItemRepository itemRepository, ItemMapper mapper, UserRepository userRepository,
                       BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    public InfoItemDto createItem(ItemDto itemDto, Long ownerId) {
        return mapper.toInfoItemDto(itemRepository.save(mapper.toItem(itemDto, ownerId)));
    }

    public InfoItemDto updateItem(ItemDto itemDto, Long ownerId) {
        Item item = itemRepository.findById(itemDto.getId()).orElseThrow(() -> new DataNotFound(
                String.format("Вещь с id %d в базе данных не обнаружена", itemDto.getId())));

        return mapper.toInfoItemDto(itemRepository.save(updateItemFromRepository(itemDto, ownerId, item)));
    }

    public InfoItemDto getItemById(Long itemId) {
        return mapper.toInfoItemDto(itemRepository.getById(itemId));
    }

    public List<InfoItemDto> getAllItemsByOwnerId(Long ownerId) {
        return itemRepository.findByOwnerId(ownerId).stream().map(mapper::toInfoItemDto).collect(Collectors.toList());
    }

    public List<InfoItemDto> searchItems(String text) {
        return itemRepository.findByNameContainsOrDescriptionContains(text, text)
                .stream().map(mapper::toInfoItemDto).collect(Collectors.toList());
    }

    public CommentDto createComment(Long itemId, Long userId, CommentDto commentDto) {
        itemRepository.findById(itemId).orElseThrow(() -> new DataNotFound(
                String.format("Вещи с id %d в базе данных не обнаружен", itemId)));
        userRepository.findById(userId).orElseThrow(() -> new DataNotFound(
                String.format("Пользователь с id %d в базе данных не обнаружен", userId)));
        if (bookingRepository.findBookingItemByBooker(userId, itemId).size() == 0) {
            throw new ValidationDataException("Оставить комментарий к вещи может только пользователь, " +
                                                "бравший её в аренду");
        }
        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(itemId, userId, commentDto)));
    }

    private Item updateItemFromRepository(ItemDto itemDto, Long ownerId, Item item) {
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new ValidationDataException("Некорректно указан собственник вещи");
        }
        if (itemDto.getName() != null) {
            item.setName(item.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return item;
    }
}
