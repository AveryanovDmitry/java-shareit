package ru.practicum.shareit.booking.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingDtoCreateNew {
    private Long id;
    private Long itemId;
    private Long userId;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
}
