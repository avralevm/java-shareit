package ru.practicum.shareit.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Set;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {
    private static Validator validator;
    private static final Long TEST_ID = 1L;
    private static final String TEST_NAME = "John Doe";
    private static final String TEST_EMAIL = "john.doe@example.com";
    private static final String EMPTY_STRING = " ";
    private static final String INVALID_EMAIL = "invalid-email";

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void constructorShouldCreateValidUserDto() {
        UserDto user = new UserDto(TEST_ID, TEST_NAME, TEST_EMAIL);

        assertEquals(TEST_ID, user.getId());
        assertEquals(TEST_NAME, user.getName());
        assertEquals(TEST_EMAIL, user.getEmail());
    }

    @Test
    void validationCreateShouldFailOnEmptyNameAndInvalidEmail() {
        UserDto invalidUser = new UserDto(TEST_ID, EMPTY_STRING, INVALID_EMAIL);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(
                invalidUser,
                UserDto.CreateValidation.class
        );

        assertEquals(2, violations.size());

        List<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        assertTrue(messages.contains("Имя не может быть пустым"));
        assertTrue(messages.contains("Некорректный формат email"));
    }

    @Test
    void validationUpdateShouldPassWithValidEmailOnly() {
        UserDto validUpdateUser = new UserDto(TEST_ID, null, TEST_EMAIL);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(
                validUpdateUser,
                UserDto.UpdateValidation.class
        );

        assertTrue(violations.isEmpty());
    }

    @Test
    void validationCreateShouldPassWithValidData() {
        UserDto validUser = new UserDto(null, TEST_NAME, TEST_EMAIL);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(
                validUser,
                UserDto.CreateValidation.class
        );

        assertTrue(violations.isEmpty());
    }

    @Test
    void validationUpdateShouldFailOnInvalidEmail() {
        UserDto invalidUpdateUser = new UserDto(TEST_ID, TEST_NAME, INVALID_EMAIL);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(
                invalidUpdateUser,
                UserDto.UpdateValidation.class
        );

        assertEquals(1, violations.size());
        assertEquals("Некорректный формат email", violations.iterator().next().getMessage());
    }

    @Test
    void validationCreateShouldFailOnNullName() {
        UserDto invalidUser = new UserDto(null, null, TEST_EMAIL);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(
                invalidUser,
                UserDto.CreateValidation.class
        );

        assertEquals(1, violations.size());
        assertEquals("Имя не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void validationCreateShouldFailOnNullEmail() {
        UserDto invalidUser = new UserDto(null, TEST_NAME, null);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(
                invalidUser,
                UserDto.CreateValidation.class
        );

        assertEquals(1, violations.size());
        assertEquals("Email не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void validationUpdateShouldPassWithNullName() {
        UserDto validUpdateUser = new UserDto(TEST_ID, null, TEST_EMAIL);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(
                validUpdateUser,
                UserDto.UpdateValidation.class
        );

        assertTrue(violations.isEmpty());
    }

    @Test
    void validationUpdateShouldPassWithEmptyName() {
        UserDto validUpdateUser = new UserDto(TEST_ID, "", TEST_EMAIL);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(
                validUpdateUser,
                UserDto.UpdateValidation.class
        );

        assertTrue(violations.isEmpty());
    }
}