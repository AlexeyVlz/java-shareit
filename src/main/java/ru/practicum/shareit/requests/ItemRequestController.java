package ru.practicum.shareit.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.InfoItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private final RequestService requestService;

    @Autowired
    public ItemRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public InfoItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Получен запрос к эндпоинту: POST: /requests");
        return requestService.createRequest(itemRequestDto, userId);

    }

    @GetMapping
    public List<InfoItemRequestDto> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос к эндпоинту: GET: /requests");
        List<InfoItemRequestDto> list = requestService.getRequestsByUserId(userId);
        return list;
    }

    @GetMapping("/all")
    public List<InfoItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                   Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10")
                                                   Integer size) {
        log.info("Получен запрос к эндпоинту: GET: /requests; from = {}, size = {}", from, size);
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("creationTime").descending());
        return requestService.getAllRequests(userId, pageRequest);
    }

    @GetMapping("/{requestId}")
    public InfoItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable @Positive Long requestId) {
        log.info("Получен запрос к эндпоинту: GET: /requests/{requestId}; requestId = {}", requestId);
        return requestService.getRequestById(requestId, userId);
    }

}
