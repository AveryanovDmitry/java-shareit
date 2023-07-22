package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoCreateNew;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeptions.BookingException;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Objects;

@RequiredArgsConstructor
@Component
class ValidatorBookingService {
    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    Item validateItemBooking(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Бронируемая вещь не найдена"));
        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new BookingException("Бронируемая вещь недоступна");
        }
        return item;
    }

    BookingDtoCreateNew validateStartAndEndBooking(BookingDtoCreateNew newBooking) {
        if (!newBooking.getStart().isBefore(newBooking.getEnd())) {
            throw new BookingException("Время старта аренды не может быть позже конца аренды");
        }
        return newBooking;
    }

    User getBookingUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден при создании запроса бронирования"));
    }

    Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронь не найдена"));
    }

    void validBookerAsOwner(Long bookerId, Item item) {
        if (Objects.equals(bookerId, item.getOwner())) {
            throw new NotFoundException("Владелец вещи не может бронировать свои вещи.");
        } else if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new BookingException(String.format("Вещь с id %d не доступна для бронирования.",
                    item.getId()));
        }
    }
}
