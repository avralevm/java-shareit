package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;
    @NotNull(message = "Текст комментария должен быть указан")
    private String text;
    private String authorName;
    private LocalDateTime created;
}
