package ru.practicum.shareit.booking.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequest {
    @NotNull(message = "Id вещи должно быть указано")
    private Long itemId;

    @NotNull(message = "Дата начала должна быть указана")
    private LocalDateTime start;

    @NotNull(message = "Дата конца должна быть указана")
    private LocalDateTime end;
}