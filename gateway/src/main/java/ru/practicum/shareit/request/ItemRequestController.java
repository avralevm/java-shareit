package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @RequestBody @Valid ItemRequestCreateDto requestDto,
            @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("[POST] Создание запроса на вещь. Пользователь id: {}, Описание: {}", userId,
                requestDto.getDescription());
        return itemRequestClient.createItemRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItemRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("[GET] Получение запросов пользователя с ID: {}", userId);
        return itemRequestClient.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsersItemRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("[GET] Получение списка запросов других пользователей. Пользователь id: {}", userId);
        return itemRequestClient.getOtherUsersItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable Long requestId,
                                                     @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("[GET] Получение запроса ID: {}. Запросил пользователь ID: {}", requestId, userId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}