package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.valid.ValidateStartAndEndBooking;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ValidateStartAndEndBooking
public class NewBooking {
    private Long itemId;
    @FutureOrPresent
    private LocalDateTime start;
    private LocalDateTime end;
}