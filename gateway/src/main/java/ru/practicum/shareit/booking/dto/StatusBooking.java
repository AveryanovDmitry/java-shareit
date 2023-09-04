package ru.practicum.shareit.booking.dto;

/**
 * WAITING — новое бронирование, ожидает одобрения,
 * APPROVED — бронирование подтверждено владельцем,
 * REJECTED — бронирование отклонено владельцем,
 * CANCELED — бронирование отменено создателем.
 */
public enum StatusBooking {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED
}
