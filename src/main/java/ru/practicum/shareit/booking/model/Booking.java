package ru.practicum.shareit.booking.model;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class Booking {
    private Long id;
    @FutureOrPresent(message = "Дата начала должна быть в будущем или настоящем")
    private LocalDateTime start;
    @FutureOrPresent(message = "Дата начала должна быть в будущем или настоящем")
    private LocalDateTime end;
    @NotNull(message = "Вещь должна быть указана")
    private Item item;
    @NotNull(message = "Пользователь должен быть указан")
    private User booker;
    private BukingStatus status;
}