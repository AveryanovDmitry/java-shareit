package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreateNew;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.GetItemBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface BookingMapper {

    BookingDto fromBookingToDto(Booking booking);

    Booking fromDtoNewCreateToModel(BookingDtoCreateNew newBooking);

    @Mapping(target = "bookerId", source = "booker.id")
    BookingDtoShort fromDtoToShort(Booking booking);

    @Mapping(target = "bookerId", source = "booker.id")
    GetItemBookingDto toSimpleDto(Booking booking);
}
