package ru.practicum.shareit.item.model;

import lombok.Data;
import org.apache.catalina.User;
import ru.practicum.shareit.request.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private Integer id;
    private String name;
    private String description;
    private boolean available;
    private User owner;
    private ItemRequest request;
}
