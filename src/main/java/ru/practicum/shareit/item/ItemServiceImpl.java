package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ExceptionAccess;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.InMemoryUserRepository;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        InMemoryUserRepository newUserRepository = (InMemoryUserRepository) userRepository;
        if (!newUserRepository.getUsers().containsKey(ownerId)) {
            throw new NullPointerException(String.format("Пользователь с id %d не обнаружен", ownerId));
        }
        return itemRepository.createItem(itemDto, ownerId);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId) {
        InMemoryItemRepository newItemRepository = (InMemoryItemRepository) itemRepository;
        if (!newItemRepository.getItems().containsKey(itemId)) {
            throw new NullPointerException(String.format("Вещь с id %d в базе отсутствует", itemId));
        }
        if (!Objects.equals(newItemRepository.getItems().get(itemId).getOwnerId(), ownerId)) {
            throw new ExceptionAccess("Вносить изменения может только владелец вещи.");
        }
        return itemRepository.updateItem(itemDto, itemId, ownerId);
    }

    @Override
    public ItemDto getItemById(Long itemId, Long ownerId) {
        InMemoryItemRepository newItemRepository = (InMemoryItemRepository) itemRepository;
        if (!newItemRepository.getItems().containsKey(itemId)) {
            throw new NullPointerException(String.format("Вещь с id %d в базе отсутствует", itemId));
        }
        if ((long) newItemRepository.getItems().get(itemId).getOwnerId() == (long) ownerId) {
            throw new ExceptionAccess("Неверно указан владелец вещи");
        }
        return itemRepository.getItemById(itemId, ownerId);
    }

    @Override
    public List<ItemDto> getAllItemsByOwnerId(Long ownerId) {
        return itemRepository.getAllItemsByOwnerId(ownerId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.equals("")) {
            return new ArrayList<>();
        }
        return itemRepository.searchItems(text);
    }
}
