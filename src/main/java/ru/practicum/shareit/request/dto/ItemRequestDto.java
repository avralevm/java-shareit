package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
    @NotNull(message = "Пользовательдолжен быть указан")
    private User requestor;
    @Past(message = "Дата создания запроса не может быть в будущем")
    private LocalDateTime created;
}
