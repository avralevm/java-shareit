package ru.practicum.shareit.item;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ItemDtoTest {
    private static Validator validator;
    private static final Long TEST_ID = 1L;
    private static final String TEST_NAME = "Test Item";
    private static final String TEST_DESCRIPTION = "Test Description";
    private static final Long TEST_OWNER_ID = 1L;
    private static final Long TEST_REQUEST_ID = 1L;
    private static final Boolean TEST_AVAILABLE = true;
    private static final String EMPTY_STRING = " ";
    private static final List<CommentDto> TEST_COMMENTS = List.of();

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void constructorShouldCreateValidItemDto() {
        ItemDto item = new ItemDto(TEST_ID, TEST_NAME, TEST_DESCRIPTION, TEST_OWNER_ID,
                TEST_REQUEST_ID, TEST_AVAILABLE, TEST_COMMENTS);

        assertEquals(TEST_ID, item.getId());
        assertEquals(TEST_NAME, item.getName());
        assertEquals(TEST_DESCRIPTION, item.getDescription());
        assertEquals(TEST_OWNER_ID, item.getOwnerId());
        assertEquals(TEST_REQUEST_ID, item.getRequestId());
        assertEquals(TEST_AVAILABLE, item.getAvailable());
        assertEquals(TEST_COMMENTS, item.getComments());
    }

    @Test
    void validationCreateShouldFailOnEmptyNameAndDescription() {
        ItemDto invalidItem = new ItemDto(TEST_ID, EMPTY_STRING, EMPTY_STRING,
                TEST_OWNER_ID, TEST_REQUEST_ID, null, TEST_COMMENTS);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(
                invalidItem,
                ItemDto.CreateValidation.class
        );

        assertEquals(3, violations.size());

        List<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        assertTrue(messages.contains("Имя не может быть пустым"));
        assertTrue(messages.contains("Описание не может быть пустым"));
        assertTrue(messages.contains("Статус не может быть пустым"));
    }

    @Test
    void validationCreateShouldPassWithValidData() {
        ItemDto validItem = new ItemDto(null, TEST_NAME, TEST_DESCRIPTION,
                null, null, TEST_AVAILABLE, null);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(
                validItem,
                ItemDto.CreateValidation.class
        );

        assertTrue(violations.isEmpty());
    }

    @Test
    void validationCreateShouldFailOnNullName() {
        ItemDto invalidItem = new ItemDto(null, null, TEST_DESCRIPTION,
                null, null, TEST_AVAILABLE, null);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(
                invalidItem,
                ItemDto.CreateValidation.class
        );

        assertEquals(1, violations.size());
        assertEquals("Имя не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void validationCreateShouldFailOnNullDescription() {
        ItemDto invalidItem = new ItemDto(null, TEST_NAME, null,
                null, null, TEST_AVAILABLE, null);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(
                invalidItem,
                ItemDto.CreateValidation.class
        );

        assertEquals(1, violations.size());
        assertEquals("Описание не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void validationCreateShouldFailOnNullAvailable() {
        ItemDto invalidItem = new ItemDto(null, TEST_NAME, TEST_DESCRIPTION,
                null, null, null, null);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(
                invalidItem,
                ItemDto.CreateValidation.class
        );

        assertEquals(1, violations.size());
        assertEquals("Статус не может быть пустым", violations.iterator().next().getMessage());
    }
}