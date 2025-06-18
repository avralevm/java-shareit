package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("[GET] Запрос на получение списка всех пользователей");
        return userClient.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        log.info("[GET] Запрос на получение пользователя с id: {}", id);
        return userClient.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(
            @Validated(UserDto.CreateValidation.class) @RequestBody UserDto userDto) {
        log.info("[POST] Создание нового пользователя: name={}, email={}", userDto.getName(), userDto.getEmail());
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(
            @Validated(UserDto.UpdateValidation.class) @RequestBody UserDto userDto,
            @PathVariable Long id) {
        log.info("[PATCH] Обновление данных пользователя с id={}, новое имя={}, новый email={}",
                id, userDto.getName(), userDto.getEmail());
        return userClient.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        log.info("[DELETE] Удаление пользователя с id={}", id);
        return userClient.deleteUser(id);
    }
}