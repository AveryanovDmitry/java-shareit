package ru.practicum.shareit.item.dto;

import lombok.Data;
import org.apache.catalina.User;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {
    private Integer id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
    private User owner;
    private ItemRequest request;
}
