package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemRequestCreateDto {
    @NotNull(message = "Текст запроса должен быть указан")
    private String description;
}