package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({UserServiceImpl.class, UserMapperImpl.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    void createUserShouldSaveAndReturnUser() {
        UserDto userDto = new UserDto(null, "Test User", "test@example.com");

        UserDto result = userService.createUser(userDto);

        assertNotNull(result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
        assertTrue(userRepository.existsById(result.getId()));
    }

    @Test
    void updateUserShouldUpdateFields() {
        User savedUser = userRepository.save(new User(null, "Old Name", "old@example.com"));
        UserDto updateDto = new UserDto(null, "New Name", null);

        UserDto result = userService.updateUser(updateDto, savedUser.getId());

        assertEquals("New Name", result.getName());
        assertEquals("old@example.com", result.getEmail());
    }

    @Test
    void getUserByIdShouldReturnUser() {
        User savedUser = userRepository.save(new User(null, "Test", "test@example.com"));

        UserDto result = userService.getUserById(savedUser.getId());

        assertEquals(savedUser.getId(), result.getId());
        assertEquals(savedUser.getName(), result.getName());
    }

    @Test
    void deleteUserShouldRemoveFromDb() {
        User savedUser = userRepository.save(new User(null, "To Delete", "delete@example.com"));

        userService.deleteUser(savedUser.getId());

        assertFalse(userRepository.existsById(savedUser.getId()));
    }

    @Test
    void findAllShouldReturnAllUsers() {
        userRepository.save(new User(null, "User 1", "user1@example.com"));
        userRepository.save(new User(null, "User 2", "user2@example.com"));

        List<UserDto> result = userService.findAll();

        assertEquals(2, result.size());
    }
}