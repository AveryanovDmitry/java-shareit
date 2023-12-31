package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBooking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeptions.BookingException;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ValidatorBookingService validator;

    private static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");

    @Transactional
    public BookingDto createBooking(NewBooking newBooking, Long bookerId) {
        Item item = validator.validateItemBooking(newBooking.getItemId());
        User user = validator.getBookingUser(bookerId);
        Booking booking = bookingMapper.fromDtoNewToModel(newBooking);
        validator.validBookerAsOwner(bookerId, item);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(StatusBooking.WAITING);
        return bookingMapper.fromBookingToDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingDto approveOrRejected(Long bookingId, boolean approved, long userId) {
        validator.getBookingUser(userId);
        Booking booking = validator.getBooking(bookingId);
        if (booking.getStatus().equals(StatusBooking.APPROVED)) {
            throw new BookingException(String.format("У бронирования с id %d уже стоит статус %s",
                    bookingId, StatusBooking.APPROVED.name()));
        }
        if (booking.getItem().getOwner() != userId) {
            throw new NotFoundException("Пользователь не владелец бронируемой вещи");
        }
        if (approved) {
            booking.setStatus(StatusBooking.APPROVED);
        } else {
            booking.setStatus(StatusBooking.REJECTED);
        }
        return bookingMapper.fromBookingToDto(booking);
    }

    public BookingDto getBookingById(Long bookingId, Long userID) {
        validator.getBookingUser(userID);
        Booking booking = validator.getBooking(bookingId);
        if (!Objects.equals(booking.getBooker().getId(), userID)
                && !Objects.equals(booking.getItem().getOwner(), userID)) {
            throw new NotFoundException("Запрос может быть выполнен либо автором бронирования, либо владельцем вещи");
        }
        return bookingMapper.fromBookingToDto(booking);
    }

    public List<BookingDto> getAllBookingByUserId(PageRequest pageable, Long userId, String stateStr) {
        validator.getBookingUser(userId);
        State state = State.checkAndConvert(stateStr.toUpperCase());
        List<Booking> bookings;
        switch (state) {
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(pageable, userId,
                        StatusBooking.WAITING, SORT_BY_START_DESC);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(pageable, userId,
                        StatusBooking.REJECTED, SORT_BY_START_DESC);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBefore(pageable, userId,
                        LocalDateTime.now(), SORT_BY_START_DESC);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfter(pageable, userId,
                        LocalDateTime.now(), SORT_BY_START_DESC);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterAndEndBefore(pageable, userId,
                        LocalDateTime.now(), SORT_BY_START_DESC);
                break;
            default:
                bookings = bookingRepository.findAllByBookerId(pageable, userId, SORT_BY_START_DESC);
        }

        return bookings
                .stream()
                .map(bookingMapper::fromBookingToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsOfOwner(PageRequest pageable, Long ownerId, String stateStr) {
        validator.getBookingUser(ownerId);
        State state = State.checkAndConvert(stateStr.toUpperCase());

        List<Booking> bookings;
        switch (state) {
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerAndStatus(pageable, ownerId,
                        StatusBooking.WAITING, SORT_BY_START_DESC);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerAndStatus(pageable, ownerId,
                        StatusBooking.REJECTED, SORT_BY_START_DESC);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerAndEndBefore(pageable, ownerId,
                        LocalDateTime.now(), SORT_BY_START_DESC);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerAndStartAfter(pageable, ownerId,
                        LocalDateTime.now(), SORT_BY_START_DESC);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(pageable, ownerId,
                        LocalDateTime.now(), SORT_BY_START_DESC);
                break;
            default:
                bookings = bookingRepository.findAllByItemOwner(pageable, ownerId, SORT_BY_START_DESC);
        }
        return bookings.stream()
                .map(bookingMapper::fromBookingToDto)
                .collect(Collectors.toList());
    }
}
