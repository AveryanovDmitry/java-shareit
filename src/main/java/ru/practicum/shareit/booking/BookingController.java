package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBooking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
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
                                                  @RequestParam(defaultValue = "All") String state,
                                                  @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                  @RequestParam(defaultValue = "10") @Min(1) @Max(20) Integer size) {
        return service.getAllBookingByUserId(PageRequest.of(from / size, size,
                        Sort.by("start").descending()),
                userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingAllByOwner(@RequestHeader(OWNER_ID) long userId,
                                                 @RequestParam(defaultValue = "All") String state,
                                                 @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(defaultValue = "10") @Min(1) @Max(20) Integer size) {
        return service.getBookingsOfOwner(PageRequest.of(from / size, size,
                Sort.by("start").descending()), userId, state);
    }
}
