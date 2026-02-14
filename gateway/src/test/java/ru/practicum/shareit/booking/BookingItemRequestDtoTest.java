package ru.practicum.shareit.booking;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BookingItemRequestDtoTest {
    private static Validator validator;
    private static final LocalDateTime FUTURE_DATE = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime PAST_DATE = LocalDateTime.now().minusDays(1);

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validationShouldPassForValidDto() {
        BookItemRequestDto dto = new BookItemRequestDto(1L, FUTURE_DATE, FUTURE_DATE.plusDays(1));
        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validationShouldFailForNullItemId() {
        BookItemRequestDto dto = new BookItemRequestDto(null, FUTURE_DATE, FUTURE_DATE.plusDays(1));
        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Id вещи должно быть указано", violations.iterator().next().getMessage());
    }

    @Test
    void validationShouldFailForPastStartDate() {
        BookItemRequestDto dto = new BookItemRequestDto(1L, PAST_DATE, FUTURE_DATE);
        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Дата начала должна быть в будущем или настоящим", violations.iterator().next().getMessage());
    }

    @Test
    void validationShouldFailForNullStartDate() {
        BookItemRequestDto dto = new BookItemRequestDto(1L, null, FUTURE_DATE);
        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Дата начала должна быть указана", violations.iterator().next().getMessage());
    }

    @Test
    void validationShouldFailForNullEndDate() {
        BookItemRequestDto dto = new BookItemRequestDto(1L, FUTURE_DATE, null);
        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Дата окончания должна быть указана", violations.iterator().next().getMessage());
    }

    @Test
    void validationShouldFailForEndBeforeStart() {
        BookItemRequestDto dto = new BookItemRequestDto(1L, FUTURE_DATE, FUTURE_DATE.minusHours(1));
        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Дата окончания должна быть после даты начала", violations.iterator().next().getMessage());
    }
}