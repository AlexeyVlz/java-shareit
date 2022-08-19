package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.Create;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@Validated({Create.class}) @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Получен запрос к эндпоинту: POST: /items");
        return itemService.createItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId, @RequestBody @Valid ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Получен запрос к эндпоинту: PATCH: /items/{itemId}");
        return itemService.updateItem(itemDto, itemId, ownerId);

    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Получен запрос к эндпоинту: GET: /items/{itemId}");
        return itemService.getItemById(itemId, ownerId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Получен запрос к эндпоинту: GET: /items");
        return itemService.getAllItemsByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Получен запрос к эндпоинту: GET: /items/search");
        return itemService.searchItems(text);
    }
}
