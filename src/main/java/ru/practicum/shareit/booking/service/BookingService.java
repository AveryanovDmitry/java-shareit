package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBooking;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(NewBooking bookingDto, Long userId);

    BookingDto approveOrRejected(Long bookingId, boolean approved, long userId);

    BookingDto getBookingById(Long bookingId, Long userID);

    List<BookingDto> getAllBookingByUserId(PageRequest pageable, Long userId, String state);

    List<BookingDto> getBookingsOfOwner(PageRequest pageable, Long ownerId, String state);
}
