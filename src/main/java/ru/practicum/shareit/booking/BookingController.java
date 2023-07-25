package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBooking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String OWNER_ID = "X-Sharer-User-Id";

    private final BookingService service;

    @PostMapping
    public BookingDto createBookingRequest(@Valid @RequestBody NewBooking bookingDto,
                                           @RequestHeader(OWNER_ID) Long userId) {
        return service.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveOrRejectBooking(@PathVariable Long bookingId, @RequestParam boolean approved,
                                             @RequestHeader(OWNER_ID) long userId) {
        return service.approveOrRejected(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId, @RequestHeader(OWNER_ID) long userId) {
        return service.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingAllByUserID(@RequestHeader(OWNER_ID) long userId,
                                                  @RequestParam(defaultValue = "All") String state) {
        return service.getAllBookingByUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingAllByOwner(@RequestHeader(OWNER_ID) long userId,
                                                 @RequestParam(defaultValue = "All") String state) {
        return service.getBookingsOfOwner(userId, state);
    }
}
