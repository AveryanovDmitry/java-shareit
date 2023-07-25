package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.valid.ValidateStartAndEndBooking;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ValidateStartAndEndBooking
public class NewBooking {
    @NotNull
    private Long itemId;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    @Future
    private LocalDateTime end;
}