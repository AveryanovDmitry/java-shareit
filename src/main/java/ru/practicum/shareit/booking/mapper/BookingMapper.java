package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.NewBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface BookingMapper {

    BookingDto fromBookingToDto(Booking booking);

    Booking fromDtoNewToModel(NewBooking newBooking);

    @Mapping(target = "bookerId", source = "booker.id")
    BookingDtoShort fromDtoToShort(Booking booking);
}
