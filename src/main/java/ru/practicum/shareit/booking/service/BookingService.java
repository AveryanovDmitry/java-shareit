package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreateNew;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingDtoCreateNew bookingDto, Long userId);

    BookingDto approveOrRejected(Long bookingId, boolean approved, long userId);

    BookingDto getBookingById(Long bookingId, Long userID);

    List<BookingDto> getAllBookingByUserId(Long userId, String state);

    List<BookingDto> getBookingsOfOwner(Long ownerId, String state);
}
