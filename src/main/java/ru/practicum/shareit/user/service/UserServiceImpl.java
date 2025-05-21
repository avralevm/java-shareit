package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static ru.practicum.shareit.user.dto.UserMapper.toUser;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getItemById(Long id) {
        User user = userRepository.getUserById(id);
        return toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = toUser(userDto);
        User createdUser = userRepository.createUser(user);
        return toUserDto(createdUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long id) {
        userDto.setId(id);
        User user = toUser(userDto);
        User updatedUser = userRepository.updateUser(user);
        return toUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteUser(id);
    }
}