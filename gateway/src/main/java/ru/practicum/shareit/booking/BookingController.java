package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.NewBooking;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.ShareItGateway.USER_ID_HEADER;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBookingRequest(@Valid @RequestBody NewBooking bookingDto,
                                                       @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingClient.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveOrRejectBooking(@PathVariable Long bookingId, @RequestParam boolean approved,
                                                         @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingClient.approveOrRejected(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable Long bookingId, @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingClient.findBookingByUserOwner(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingAllByUserID(@RequestHeader(USER_ID_HEADER) long userId,
                                                        @RequestParam(defaultValue = "All") String state,
                                                        @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                        @RequestParam(defaultValue = "10") @Min(1) @Max(20) Integer size) {
        return bookingClient.getAllBookingByUserId(size, from, userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingAllByOwner(@RequestHeader(USER_ID_HEADER) long userId,
                                                       @RequestParam(defaultValue = "All") String state,
                                                       @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                       @RequestParam(defaultValue = "10") @Min(1) @Max(20) Integer size) {
        return bookingClient.findBookingsByUserId(size, from, userId, state);
    }
}
