package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.Create;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InfoItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;

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
    public InfoItemDto createItem(@Validated({Create.class}) @RequestBody ItemDto itemDto,
                                  @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Получен запрос к эндпоинту: POST: /items");
        return itemService.createItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public InfoItemDto updateItem(@PathVariable Long itemId, @RequestBody @Valid ItemDto itemDto,
                                  @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Получен запрос к эндпоинту: PATCH: /items/{itemId}");
        itemDto.setId(itemId);
        return itemService.updateItem(itemDto, ownerId);

    }

    @GetMapping("/{itemId}")
    public InfoItemDto getItemById(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Получен запрос к эндпоинту: GET: /items/{itemId}");
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<InfoItemDto> getAllItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Получен запрос к эндпоинту: GET: /items");
        return itemService.getAllItemsByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List<InfoItemDto> searchItems(@RequestParam String text) {
        log.info("Получен запрос к эндпоинту: GET: /items/search");
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody CommentDto commentDto) {
        log.info("Получен запрос к эндпоинту: POST: /items/{itemId}/comment");
        return itemService.createComment(itemId, userId, commentDto);
    }

}
