package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ItemRequestDto {
    @NotBlank(message = "описание не должно быть пустым")
    @Size(max = 1000, message = "Превышена максимальная длина сообщения")
    private String description;
}
