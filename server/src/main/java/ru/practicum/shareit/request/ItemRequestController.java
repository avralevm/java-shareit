package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestCreate;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestBody ItemRequestCreate itemRequestCreate,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("[POST] Создание запроса на вещь. Пользователь ID: {}, Описание: {}",
                userId, itemRequestCreate.getDescription());
        return itemRequestService.createItemRequest(itemRequestCreate, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("[GET] Получение запросов пользователя с ID: {}", userId);
        return itemRequestService.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOtherUsersItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("[GET] Получение списка запросов, созданных другими пользователями");
        return itemRequestService.getOtherUsersItemRequests(userId);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable Long requestId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("[GET] Получение запроса ID: {}. Запросил пользователь ID: {}", requestId, userId);
        return itemRequestService.getItemRequestById(requestId, userId);
    }
}