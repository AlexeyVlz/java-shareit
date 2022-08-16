package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ExceptionAccess;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
public class InMemoryItemRepository implements ItemRepository {

    Long generatedId = 0L;
    Map<Long, Item> items = new HashMap<>();
    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        itemDto.setId(++generatedId);
        Item item = ItemMapper.mapItemDtoToItem(itemDto, ownerId);
        items.put(item.getId(), item);
        return ItemMapper.mapItemToItemDto(item);
    }
    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId) {
        if(!items.containsKey(itemId)){
            throw new NullPointerException(String.format("Вещь с id %d в базе отсутствует", itemId));
        }
        if(!Objects.equals(items.get(itemId).getOwnerId(), ownerId)){
            throw new ExceptionAccess("Вносить изменения может только владелец вещи.");
        }
        Item item = items.get(itemId);
        if(itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if(itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if(itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        items.put(item.getId(), item);
        return ItemMapper.mapItemToItemDto(item);
    }
    @Override
    public ItemDto getItemById(Long itemId, Long ownerId) {
        if(!items.containsKey(itemId)){
            throw new NullPointerException(String.format("Вещь с id %d в базе отсутствует", itemId));
        }
        if(!Objects.equals(items.get(itemId).getOwnerId(), ownerId)){
            throw new ExceptionAccess("Неверно указан владелец вещи");
        }
        return ItemMapper.mapItemToItemDto(items.get(itemId));
    }
    @Override
    public List<ItemDto> getAllItemsByOwnerId(Long ownerId) {
        List<ItemDto> itemsList = new ArrayList<>();
        for(Item item : items.values()){
            if(item.getOwnerId().equals(ownerId)){
                itemsList.add(ItemMapper.mapItemToItemDto(item));
            }
        }
        return itemsList;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<ItemDto> itemsList = new ArrayList<>();
        for(Item item : items.values()){
            if(item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase())) {
                itemsList.add(ItemMapper.mapItemToItemDto(item));
            }
        }
        return itemsList;
    }
}
