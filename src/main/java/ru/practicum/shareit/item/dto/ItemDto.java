package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDto {
    private Long id;
    @NotBlank(groups = {CreateValidation.class}, message = "Имя не может быть пустым")
    private String name;
    @NotBlank(groups = {CreateValidation.class}, message = "Описание не может быть пустым")
    private String description;
    @NotNull(groups = {CreateValidation.class}, message = "Статус не может быть пустым")
    private Boolean available;

    public interface CreateValidation {}
}