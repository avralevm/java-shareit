package ru.practicum.shareit.user.repository;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long id) {
        validateUser(id);
        return users.get(id);
    }

    @Override
    public User createUser(User user) {
        validateEmail(user);
        user.setId(++id);
        users.put(user.getId(), user);
        log.info("Пользователь с id = {} создан", user.getId());
        return user;
    }

    @Override
    public User updateUser(User updatedUser) {
        validateUser(updatedUser.getId());

        User user = users.get(updatedUser.getId());
        if (updatedUser.getName() != null) {
            user.setName(updatedUser.getName());
        }
        if (updatedUser.getEmail() != null) {
            validateEmail(updatedUser);
            user.setEmail(updatedUser.getEmail());
        }

        users.put(user.getId(), user);
        log.info("Пользователь с id = {} обновлён", user.getId());
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        validateUser(id);
        users.remove(id);
        log.info("Пользователь с id = {} удалён", id);
    }

    private void validateUser(Long id) {
        if (id == null) {
            log.error("Id пользователя не указан");
            throw new ValidationException("Id должен быть указан");
        }

        if (!users.containsKey(id)) {
            log.error("Пользователь с id = {} не найден", id);
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
    }

    private void validateEmail(User user) {
        users.values().stream()
                .filter(currentUser -> !currentUser.getId().equals(user.getId()))
                .filter(currentUser -> currentUser.getEmail().equals(user.getEmail()))
                .findFirst()
                .ifPresent(existingUser -> {
                    throw new DuplicateException("Пользователь с такой почтой уже существует");
                });
    }
}