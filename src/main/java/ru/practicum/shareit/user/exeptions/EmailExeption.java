package ru.practicum.shareit.user.exeptions;

public class EmailExeption extends RuntimeException {
    public EmailExeption(String message) {
        super(message);
    }
}
