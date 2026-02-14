package ru.practicum.shareit.request.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestCreate {
    @NotNull(message = "Текст запроса должен быть указан")
    private String description;
}
