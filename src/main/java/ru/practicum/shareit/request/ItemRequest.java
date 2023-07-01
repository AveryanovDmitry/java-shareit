package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 * description - текст запроса, содержащий описание требуемой вещи;
 * requestor - пользователь, создавший запрос;
 * created - дата и время создания запроса;
 */
@Data
public class ItemRequest {
    Integer id;
    String description;
    User requestor;
    LocalDateTime created;
}
