package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InfoCommentDto;
import ru.practicum.shareit.item.dto.InfoItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public InfoItemDto createItem(@RequestBody ItemDto itemDto,
                                  @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Получен запрос к эндпоинту: POST: /items");
        return itemService.createItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public InfoItemDto updateItem(@PathVariable Long itemId, @RequestBody ItemDto itemDto,
                                  @RequestHeader(name = "X-Sharer-User-Id", required = false) Long ownerId) {
        log.info("Получен запрос к эндпоинту: PATCH: /items/{itemId}");
        itemDto.setId(itemId);
        return itemService.updateItem(itemDto, ownerId);

    }

    @GetMapping("/{itemId}")
    public InfoItemDto getItemById(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос к эндпоинту: GET: /items/{itemId}");
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<InfoItemDto> getAllItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                  @RequestParam(name = "from", defaultValue = "0")
                                                  Integer from,
                                                  @RequestParam(name = "size", defaultValue = "10")
                                                  Integer size) {
        log.info("Получен запрос к эндпоинту: GET: /items");
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        return itemService.getAllItemsByOwnerId(ownerId, pageRequest);
    }

    @GetMapping("/search")
    public List<InfoItemDto> searchItems(@RequestParam String text,
                                         @RequestParam(name = "from", defaultValue = "0")
                                         Integer from,
                                         @RequestParam(name = "size", defaultValue = "10")
                                         Integer size) {
        log.info("Получен запрос к эндпоинту: GET: /items/search");
        if (text.equals("")) {
            return new ArrayList<>();
        }
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        return itemService.searchItems(text, pageRequest);
    }

    @PostMapping("/{itemId}/comment")
    public InfoCommentDto createComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestBody CommentDto commentDto) {
        log.info("Получен запрос к эндпоинту: POST: /items/{itemId}/comment");
        return itemService.createComment(itemId, userId, commentDto);
    }

}
