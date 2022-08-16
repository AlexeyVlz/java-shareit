package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDto mapItemToItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public static Item mapItemDtoToItem(ItemDto itemDto, Long ownerId) {
        return new Item(itemDto.getId(), ownerId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
    }
}
