package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exeptions.BookingException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State checkAndConvert(String source) {
        try {
            return State.valueOf(source);
        } catch (Exception e) {
            throw new BookingException(String.format("Unknown state: %S", source));
        }
    }
}
