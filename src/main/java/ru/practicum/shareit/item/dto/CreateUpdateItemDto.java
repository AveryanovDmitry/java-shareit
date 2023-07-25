package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreateUpdateItemDto {
    public interface Create {
    }

    @NotBlank(groups = {Create.class})
    @Size(max = 255)
    private String name;

    @NotBlank(groups = {Create.class})
    @Size(max = 1000)
    private String description;

    @NotNull(groups = {Create.class})
    private Boolean available;
}