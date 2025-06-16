package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemOwnerDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("[GET] Запрос на получение всех предметов владельца c id: {}", userId);
        return itemService.getOwnerItems(userId);
    }

    @GetMapping("/{id}")
    public ItemOwnerDto getItemById(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("[GET] Запрос на получение предмета по id: {}", id);
        return itemService.getItemById(id, userId);
    }

    @PostMapping
    public ItemDto createItem(@Validated(ItemDto.CreateValidation.class) @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("[POST] Создание предмета");
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
        log.info("[GET] Запрос на получение предметов по тексту: {}", text);
        return itemService.searchItemsByText(text);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto createComment(@PathVariable Long itemId, @RequestBody CommentDto commentDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("[POST] Создание комментария для предмета с ID: {} от пользователя с ID: {}", itemId, userId);
        return itemService.createComment(itemId, commentDto, userId);
    }
}