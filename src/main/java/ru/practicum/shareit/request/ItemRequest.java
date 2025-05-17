package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private Long id;
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    @NotNull(message = "Пользователь создавший запрос должен быть указан")
    private User requestor;
    @Past(message = "Дата создания запроса не может быть в будущем")
    private LocalDateTime created;
}