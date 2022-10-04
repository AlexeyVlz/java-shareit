package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.Create;


@Slf4j
@RestController
@RequestMapping(path = "/users")
public class GatewayUserController {

    private final UserClient userClient;

    @Autowired
    public GatewayUserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated({Create.class}) @RequestBody GatewayUserDto userDto) {
        log.info("Получен запрос к эндпоинту: POST: /users");
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @RequestBody GatewayUserDto userDto) {
        log.info("Получен запрос к эндпоинту: PATCH: /users/{id}");
        return userClient.updateUser(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Получен запрос к эндпоинту: DELETE: /users/{id}");
        userClient.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Получен запрос к эндпоинту: GET: /users/{id}");
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Получен запрос к эндпоинту: GET: /users");
        return userClient.getAllUsers();
    }
}
