package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.valid.ValidateStartAndEndBooking;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@ValidateStartAndEndBooking
public class NewBooking {
    private Long itemId;
    @FutureOrPresent
    private LocalDateTime start;
    private LocalDateTime end;
}