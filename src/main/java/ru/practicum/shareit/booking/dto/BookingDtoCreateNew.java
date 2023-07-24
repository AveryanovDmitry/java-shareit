package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.valid.ValidateStartAndEndBooking;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ValidateStartAndEndBooking
public class BookingDtoCreateNew {
    private Long id;
    private Long itemId;
    private Long userId;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    @Future
    private LocalDateTime end;
}
