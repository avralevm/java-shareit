package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(groups = {CreateValidation.class}, message = "Имя не может быть пустым")
    private String name;
    @NotBlank(groups = {CreateValidation.class}, message = "Описание не может быть пустым")
    private String description;
    private Long ownerId;
    private Long requestId;
    @NotNull(groups = {CreateValidation.class}, message = "Статус не может быть пустым")
    private Boolean available;
    private List<CommentDto> comments;

    public interface CreateValidation {}
}
