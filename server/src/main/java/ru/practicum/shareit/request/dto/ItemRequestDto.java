package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    @NotNull(message = "Пользователь должен быть указан")
    private Long requestorId;
    @Past(message = "Дата создания запроса не может быть в будущем")
    private LocalDateTime created;

    List<ItemDto> items;
}
