package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestWasCreatedDto {
    private Long id;
    private String description;
    private Long requester;
    private LocalDateTime created;
}
