package ru.practicum.shareit.booking.dto;

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
            throw new IllegalArgumentException(String.format("Unknown state: %S", source));
        }
    }
}
