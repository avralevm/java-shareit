package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(Long id) {
        return userMapper.toUserDto(findUserOrThrow(id));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        validateEmailUniqueness(userDto.getEmail(), null);
        User user = userRepository.save(userMapper.toUser(userDto));
        log.info("Создан пользователь с id {}", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long id) {
        User user = findUserOrThrow(id);

        validateEmailUniqueness(userDto.getEmail(), user);

        Optional.ofNullable(userDto.getEmail()).ifPresent(user::setEmail);
        Optional.ofNullable(userDto.getName()).ifPresent(user::setName);

        User updatedUser = userRepository.save(user);
        log.info("Обновлен пользователь с id {}", id);

        return userMapper.toUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
        userRepository.deleteById(id);
        log.info("Пользователь с id {} был удален", id);
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь с id {} не найден", id);
                    return new NotFoundException(String.format("Пользователь с id = %d не найден", id));
                });
    }

    private void validateEmailUniqueness(String email, User currentUser) {
        Optional.ofNullable(email)
                .filter(e -> currentUser == null || !e.equals(currentUser.getEmail()))
                .ifPresent(e -> {
                    if (userRepository.existsByEmail(e)) {
                        log.warn("Дублирование email: {}", e);
                        throw new DuplicateException("Пользователь с такой почтой уже существует");
                    }
                });
    }
}