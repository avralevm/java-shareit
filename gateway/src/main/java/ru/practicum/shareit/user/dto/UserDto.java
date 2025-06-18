package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;

    @NotBlank(groups = CreateValidation.class, message = "Имя не может быть пустым")
    private String name;

    @NotBlank(groups = CreateValidation.class, message = "Email не может быть пустым")
    @Email(groups = {CreateValidation.class, UpdateValidation.class}, message = "Некорректный формат email")
    private String email;

    public interface CreateValidation {}

    public interface UpdateValidation {}
}