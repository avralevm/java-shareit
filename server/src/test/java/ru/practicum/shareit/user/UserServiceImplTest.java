package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
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

    private User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = userRepository.save(new User(null, "Test User", "test@example.com"));
    }

    @Test
    void createUserShouldSaveAndReturnUser() {
        UserDto userDto = new UserDto(null, "New User", "new@example.com");

        UserDto result = userService.createUser(userDto);

        assertNotNull(result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
        assertTrue(userRepository.existsById(result.getId()));
    }

    @Test
    void createUserShouldThrowWhenEmailExists() {
        UserDto userDto = new UserDto(null, "Duplicate Email", savedUser.getEmail());

        assertThrows(DuplicateException.class, () -> userService.createUser(userDto));
    }


    @Test
    void updateUserShouldUpdateFields() {
        UserDto updateDto = new UserDto(null, "Updated Name", null);

        UserDto result = userService.updateUser(updateDto, savedUser.getId());

        assertEquals("Updated Name", result.getName());
        assertEquals(savedUser.getEmail(), result.getEmail());
    }

    @Test
    void updateUserShouldUpdateOnlyEmailWhenNameIsNull() {
        UserDto updateDto = new UserDto(null, null, "updated@example.com");

        UserDto result = userService.updateUser(updateDto, savedUser.getId());

        assertEquals(savedUser.getName(), result.getName());
        assertEquals("updated@example.com", result.getEmail());
    }

    @Test
    void updateUserShouldThrowWhenUserNotFound() {
        Long nonExistentUserId = 999L;
        UserDto updateDto = new UserDto(null, "Name", "email@example.com");

        assertThrows(NotFoundException.class, () ->
                userService.updateUser(updateDto, nonExistentUserId));
    }

    @Test
    void updateUserShouldThrowWhenEmailExistsForOtherUser() {
        User anotherUser = userRepository.save(new User(null, "Another", "another@example.com"));
        UserDto updateDto = new UserDto(null, "Name", anotherUser.getEmail());

        assertThrows(DuplicateException.class, () -> userService.updateUser(updateDto, savedUser.getId()));
    }

    @Test
    void getUserByIdShouldReturnUser() {
        UserDto result = userService.getUserById(savedUser.getId());

        assertEquals(savedUser.getId(), result.getId());
        assertEquals(savedUser.getName(), result.getName());
        assertEquals(savedUser.getEmail(), result.getEmail());
    }

    @Test
    void getUserByIdShouldThrowWhenUserNotFound() {
        Long nonExistentUserId = 999L;

        assertThrows(NotFoundException.class, () -> userService.getUserById(nonExistentUserId));
    }

    @Test
    void deleteUserShouldRemoveFromDb() {
        userService.deleteUser(savedUser.getId());

        assertFalse(userRepository.existsById(savedUser.getId()));
    }

    @Test
    void deleteUserShouldThrowWhenUserNotFound() {
        Long nonExistentUserId = 999L;

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.deleteUser(nonExistentUserId));

        assertTrue(exception.getMessage().contains(String.valueOf(nonExistentUserId)));
    }

    @Test
    void findAllShouldReturnAllUsers() {
        userRepository.save(new User(null, "User 1", "user1@example.com"));
        userRepository.save(new User(null, "User 2", "user2@example.com"));

        List<UserDto> result = userService.findAll();

        assertEquals(3, result.size()); // Includes the user from @BeforeEach
        assertTrue(result.stream().anyMatch(u -> u.getName().equals("Test User")));
        assertTrue(result.stream().anyMatch(u -> u.getName().equals("User 1")));
        assertTrue(result.stream().anyMatch(u -> u.getName().equals("User 2")));
    }

    @Test
    void findAllShouldReturnEmptyListWhenNoUsers() {
        userRepository.deleteAll();

        List<UserDto> result = userService.findAll();

        assertTrue(result.isEmpty());
    }

    // Additional tests for edge cases
    @Test
    void createUserShouldHandleEmptyName() {
        UserDto userDto = new UserDto(null, "", "empty@example.com");

        UserDto result = userService.createUser(userDto);

        assertEquals("", result.getName());
        assertNotNull(result.getId());
    }

    @Test
    void updateUserShouldHandleEmptyName() {
        UserDto updateDto = new UserDto(null, "", null);

        UserDto result = userService.updateUser(updateDto, savedUser.getId());

        assertEquals("", result.getName());
        assertEquals(savedUser.getEmail(), result.getEmail());
    }
}