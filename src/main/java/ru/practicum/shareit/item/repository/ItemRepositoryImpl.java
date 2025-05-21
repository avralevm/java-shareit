package ru.practicum.shareit.item.repository;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    @Override
    public List<Item> getOwnerItems(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .toList();
    }

    @Override
    public Item getItemById(Long id) {
        validateItem(id);
        return items.get(id);
    }

    @Override
    public Item createItem(Item item) {
        item.setId(++id);
        items.put(item.getId(), item);
        log.info("Вещь с id = {} создана", item.getId());
        return item;
    }

    @Override
    public Item updateItem(Item updatedItem) {
        validateItem(updatedItem.getId());

        Item item = items.get(updatedItem.getId());
        if (updatedItem.getName() != null) {
            item.setName(updatedItem.getName());
        }
        if (updatedItem.getDescription() != null) {
            item.setDescription(updatedItem.getDescription());
        }
        if (updatedItem.getAvailable() != null) {
            item.setAvailable(updatedItem.getAvailable());
        }
        return item;
    }

    @Override
    public void deleteItem(Long id) {
        validateItem(id);
        items.remove(id);
        log.info("Пользователь с id = {} удалён", id);
    }

    @Override
    public List<Item> searchItemsByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(item -> item.getAvailable() == true)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .toList();
    }


    private void validateItem(Long id) {
        if (id == null) {
            log.error("Id вещи не указан");
            throw new ValidationException("Id должен быть указан");
        }

        if (!items.containsKey(id)) {
            log.error("Вещь с id = {} не найдена", id);
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
    }
}