package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static ru.practicum.shareit.item.dto.ItemMapper.toItem;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getOwnerItems(Long userId) {
        return itemRepository.getOwnerItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto getItemById(Long id) {
        Item item = itemRepository.getItemById(id);
        return toItemDto(item);
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        User owner = userRepository.getUserById(userId);
        Item item = toItem(itemDto);
        item.setOwner(owner);
        Item createdItem = itemRepository.createItem(item);
        return toItemDto(createdItem);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId) {
        itemDto.setId(itemId);
        User owner = userRepository.getUserById(userId);
        Item item = toItem(itemDto);
        item.setOwner(owner);
        Item updatedItem = itemRepository.updateItem(item);
        return toItemDto(updatedItem);
    }

    @Override
    public void deleteItem(Long id) {
        itemRepository.deleteItem(id);
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        return itemRepository.searchItemsByText(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }
}