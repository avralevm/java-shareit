package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto getItemById(Long id);

    UserDto createUser(UserDto user);

    UserDto updateUser(UserDto user, Long id);

    void deleteUser(Long id);
}