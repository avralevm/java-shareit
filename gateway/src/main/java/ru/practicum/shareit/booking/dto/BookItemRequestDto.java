package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
    @NotNull(message = "Id вещи должно быть указано")
    private Long itemId;

    @NotNull(message = "Дата начала должна быть указана")
    @FutureOrPresent(message = "Дата начала должна быть в будущем или настоящим")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания должна быть указана")
    @Future(message = "Дата окончания должна быть в будущем")
    private LocalDateTime end;

    @AssertTrue(message = "Дата окончания должна быть после даты начала")
    private boolean isEndAfterStart() {
        return end == null || start == null || end.isAfter(start);
    }
}