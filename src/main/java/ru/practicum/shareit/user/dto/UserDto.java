package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class UserDto {
    public interface Create {
    }

    public interface Update {
    }

    private Long id;
    @NotBlank(groups = {Create.class})
    @Size(groups = {Create.class, Update.class}, max = 255)
    private String name;
    @Email(groups = {Create.class, Update.class})
    @NotEmpty(groups = {Create.class})
    @Size(groups = {Create.class, Update.class}, max = 512)
    private String email;
}
