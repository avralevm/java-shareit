package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getOwnerItems(Long userId);

    ItemDto getItemById(Long id);

    ItemDto createItem(ItemDto item, Long userId);

    ItemDto updateItem(ItemDto item, Long userId, Long id);

    void deleteItem(Long id);

    List<ItemDto> searchItemsByText(String text);
}