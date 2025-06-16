package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;

import java.util.List;

public interface ItemService {
    List<ItemOwnerDto> getOwnerItems(Long userId);

    ItemOwnerDto getItemById(Long itemId, Long userId);

    ItemDto createItem(ItemDto item, Long userId);

    ItemDto updateItem(ItemDto item, Long userId, Long id);

    void deleteItem(Long id);

    List<ItemDto> searchItemsByText(String text);

    CommentDto createComment(Long itemId, CommentDto comment, Long userId);
}