package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        return itemRepository.createItem(itemDto, ownerId);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId) {
        return itemRepository.updateItem(itemDto, itemId, ownerId);
    }

    @Override
    public ItemDto getItemById(Long itemId, Long ownerId) {
        return itemRepository.getItemById(itemId, ownerId);
    }

    @Override
    public List<ItemDto> getAllItemsByOwnerId(Long ownerId) {
        return itemRepository.getAllItemsByOwnerId(ownerId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemRepository.searchItems(text);
    }
}
