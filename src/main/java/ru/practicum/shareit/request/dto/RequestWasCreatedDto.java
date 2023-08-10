package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class RequestWasCreatedDto {
    private Long id;
    private String description;
    private User requester;
    private LocalDateTime created;
}
