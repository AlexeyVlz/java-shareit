package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {

    private Long generatedId = 0L;

    public ItemDto mapItemToItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public Item mapItemDtoToItem(ItemDto itemDto, Long ownerId) {
        return new Item(++generatedId, ownerId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
    }
}
