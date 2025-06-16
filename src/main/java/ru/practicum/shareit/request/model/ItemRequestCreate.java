package ru.practicum.shareit.request.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemRequestCreate {
    @NotNull(message = "Текст запроса должен быть указан")
    private String description;
}
