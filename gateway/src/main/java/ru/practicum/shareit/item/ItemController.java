package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoCreate;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("[GET] Запрос на получение всех предметов владельца с id: {}", userId);
        return itemClient.getOwnerItems(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("[GET] Запрос на получение предмета по id: {}", id);
        return itemClient.getItemById(id, userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@Validated(ItemDto.CreateValidation.class) @RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("[POST] Создание нового предмета: {}", itemDto.getName());
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long id) {
        log.info("[PATCH] Обновление предмета с id: {}", id);
        return itemClient.updateItem(id, itemDto, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long id) {
        log.info("[DELETE] Удаление предмета с id: {}", id);
        return itemClient.deleteItem(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByText(@RequestParam String text) {
        log.info("[GET] Поиск предметов по тексту: '{}'", text);
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId, @RequestBody CommentDtoCreate commentDto,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("[POST] Создание комментария для предмета с id: {}", itemId);
        return itemClient.createComment(itemId, commentDto, userId);
    }
}
