package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> findAll() {
        log.info("[GET] Запрос на получение всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        log.info("[GET] Запрос на получение пользователя по id: {}", id);
        return userService.getUserById(id);
    }

    @PostMapping
    public UserDto createUser(@Validated(UserDto.CreateValidation.class) @RequestBody UserDto userDto) {
        log.info("[POST] Создание пользователя");
        return userService.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@Validated(UserDto.UpdateValidation.class) @RequestBody UserDto userDto,
                              @PathVariable Long id) {
        log.info("[PATCH] Обновлены данные пользователя c ID: {}", id);
        return userService.updateUser(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("[DELETE] Удаление данных пользователя c ID: {}", id);
        userService.deleteUser(id);
    }
}