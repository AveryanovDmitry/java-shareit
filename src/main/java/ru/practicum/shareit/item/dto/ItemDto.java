package ru.practicum.shareit.item.dto;

import lombok.Data;
import org.apache.catalina.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private Integer request;
}
