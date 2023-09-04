package ru.practicum.shareit.exeptions;

public class BookingException extends RuntimeException {
    public BookingException(String message) {
        super(message);
    }
}
