package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemForBookingDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BookingDtoShort {
    private long id;
    private ItemForBookingDto item;
    private long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}