package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@Builder
public class CreateUpdateItemDto {
    public interface Create {
    }

    public interface Update {
    }

    @Pattern(regexp = "^[^ ].*[^ ]$", message = "Некорректное имя", groups = {Create.class, Update.class})
    @NotBlank(groups = {Create.class})
    @Size(max = 255, groups = {Create.class, Update.class})
    private String name;

    @Pattern(regexp = "^[^ ].*[^ ]$", message = "Некорректное имя", groups = {Create.class, Update.class})
    @NotBlank(groups = {Create.class})
    @Size(max = 255, groups = {Create.class, Update.class})
    private String description;

    @NotNull(groups = {Create.class})
    private Boolean available;

    @Min(value = 1, groups = {Create.class, Update.class})
    private Long requestId;
}