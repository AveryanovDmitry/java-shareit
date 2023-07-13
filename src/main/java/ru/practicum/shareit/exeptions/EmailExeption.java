package ru.practicum.shareit.exeptions;

public class EmailExeption extends RuntimeException {
    public EmailExeption(String message) {
        super(message);
    }
}
