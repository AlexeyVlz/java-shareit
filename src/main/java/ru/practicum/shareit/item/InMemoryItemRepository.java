package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private final ItemMapper mapper;

    @Autowired
    public InMemoryItemRepository(ItemMapper mapper) {
        this.mapper = mapper;
    }

    public Map<Long, Item> getItems() {
        return items;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        Item item = mapper.mapItemDtoToItem(itemDto, ownerId);
        items.put(item.getId(), item);
        return mapper.mapItemToItemDto(item);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId) {
        Item item = items.get(itemId);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        items.put(item.getId(), item);
        return mapper.mapItemToItemDto(item);
    }

    @Override
    public ItemDto getItemById(Long itemId, Long ownerId) {
        return mapper.mapItemToItemDto(items.get(itemId));
    }

    @Override
    public List<ItemDto> getAllItemsByOwnerId(Long ownerId) {
        List<ItemDto> itemsList = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwnerId().equals(ownerId)) {
                itemsList.add(mapper.mapItemToItemDto(item));
            }
        }
        return itemsList;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<ItemDto> itemsList = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getAvailable()) {
                if (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())) {
                    itemsList.add(mapper.mapItemToItemDto(item));
                }
            }
        }
        return itemsList;
    }
}
