package ru.practicum.shareit.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestDtoTest {
    private static Validator validator;
    private static final String VALID_DESCRIPTION = "Need a drill for home project";
    private static final String EMPTY_DESCRIPTION = "";
    private static final String BLANK_DESCRIPTION = " ";

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidItemRequestCreateDto() {
        ItemRequestCreateDto request = new ItemRequestCreateDto(VALID_DESCRIPTION);

        assertEquals(VALID_DESCRIPTION, request.getDescription());
    }

    @Test
    void shouldFailValidationWhenDescriptionIsNull() {
        ItemRequestCreateDto invalidRequest = new ItemRequestCreateDto(null);

        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(invalidRequest);

        assertEquals(1, violations.size());
        assertEquals("Текст запроса должен быть указан", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenDescriptionIsEmpty() {
        ItemRequestCreateDto invalidRequest = new ItemRequestCreateDto(EMPTY_DESCRIPTION);

        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(invalidRequest);

        assertEquals(1, violations.size());
        assertEquals("Текст запроса должен быть указан", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenDescriptionIsBlank() {
        ItemRequestCreateDto invalidRequest = new ItemRequestCreateDto(BLANK_DESCRIPTION);

        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(invalidRequest);

        assertEquals(1, violations.size());
        assertEquals("Текст запроса должен быть указан", violations.iterator().next().getMessage());
    }

    @Test
    void shouldPassValidationWithValidDescription() {
        ItemRequestCreateDto validRequest = new ItemRequestCreateDto(VALID_DESCRIPTION);

        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(validRequest);

        assertTrue(violations.isEmpty());
    }
}