package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class RequestWithItemsDto {
    private Integer id;
    private String description;
    private LocalDateTime created;
    private List<ItemResponseDto> items;
}
