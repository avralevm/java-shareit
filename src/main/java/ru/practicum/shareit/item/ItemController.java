package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("[GET] Запрос на получение всех вещей");
        return itemService.getOwnerItems(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable Long id) {
        log.info("[GET] Запрос на получение вещи по id: {}", id);
        return itemService.getItemById(id);
    }

    @PostMapping
    public ItemDto createItem(@Validated(ItemDto.CreateValidation.class) @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("[POST] Создание вещи");
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long id) {
        log.info("[PATCH] Обновлены данные пользователя c ID: {}", id);
        return itemService.updateItem(itemDto, userId, id);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        log.info("[DELETE] Удаление данных пользователя c ID: {}", id);
        itemService.deleteItem(id);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByText(@RequestParam String text) {
        log.info("[GET] Запрос на получение вещей по тексту: {}", text);
        return itemService.searchItemsByText(text);
    }
}