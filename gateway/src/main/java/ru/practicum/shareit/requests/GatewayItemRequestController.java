package ru.practicum.shareit.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Slf4j
@RestController
@RequestMapping(path = "/requests")
@Validated
public class GatewayItemRequestController {

    private final RequestClient requestClient;

    @Autowired
    public GatewayItemRequestController(RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody @Valid GatewayItemRequestDto itemRequestDto) {
        log.info("Получен запрос к эндпоинту: POST: /requests");
        return requestClient.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос к эндпоинту: GET: /requests");
        return requestClient.getRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                   Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10")
                                                   Integer size) {
        log.info("Получен запрос к эндпоинту: GET: /requests; from = {}, size = {}", from, size);
        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable @Positive Long requestId) {
        log.info("Получен запрос к эндпоинту: GET: /requests/{requestId}; requestId = {}", requestId);
        return requestClient.getRequestById(requestId, userId);
    }

}
