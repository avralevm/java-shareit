package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;
    @FutureOrPresent(message = "Дата начала должна быть в будущем или настоящем")
    private LocalDateTime start;
    @Future(message = "Дата окончания должна быть в будущем")
    private LocalDateTime end;
    @NotNull(message = "Вещь должна быть указана")
    private ItemDto item;
    @NotNull(message = "Пользователь должен быть указан")
    private UserDto booker;
    private BookingStatus status;
}