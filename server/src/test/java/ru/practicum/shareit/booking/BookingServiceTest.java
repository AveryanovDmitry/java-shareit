package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exeptions.BookingException;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BookingServiceTest {
    private final BookingServiceImpl bookingService;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private NewBooking newBooking;
    private Item item;
    private Item item2;
    private User user;
    private User user2;

    @BeforeEach
    public void setUp() {
        user = new User(1L, "test", "test@yandex.ru");
        user2 = new User(2L, "test2", "test2@yandex.ru");

        item = Item.builder()
                .name("item test")
                .description("item test description")
                .available(Boolean.TRUE)
                .owner(user.getId())
                .build();
        item2 = Item.builder()
                .name("Item2 test")
                .description("item2 test description")
                .available(Boolean.TRUE)
                .owner(user2.getId())
                .build();

        newBooking = new NewBooking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));
    }

    @Test
    void createBooking() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        BookingDto savedBooking = bookingService.createBooking(newBooking, user2.getId());
        BookingDto findBooking = bookingService
                .getBookingById(savedBooking.getId(), user2.getId());

        assertThat(savedBooking).usingRecursiveComparison()
                .ignoringFields("start", "end").isEqualTo(findBooking);
    }

    @Test
    void createBookingFalseAvailable() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        BookingDto savedBooking = bookingService.createBooking(newBooking, user2.getId());
        BookingDto findBooking = bookingService
                .getBookingById(savedBooking.getId(), user2.getId());

        assertThat(savedBooking).usingRecursiveComparison()
                .ignoringFields("start", "end").isEqualTo(findBooking);
    }

    @Test
    void notExistingItem() {
        newBooking.setItemId(2L);
        userRepository.save(user2);
        assertThatThrownBy(() -> bookingService.createBooking(newBooking, user2.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void bookerIsOwner() {
        userRepository.save(user);
        itemRepository.save(item);
        assertThatThrownBy(() -> bookingService.createBooking(newBooking, user.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void notExistingBooker() {
        userRepository.save(user);
        itemRepository.save(item);
        assertThatThrownBy(() -> bookingService.createBooking(newBooking, 2L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void notAvailableItem() {
        item.setAvailable(Boolean.FALSE);
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        assertThatThrownBy(() -> bookingService.createBooking(newBooking, user2.getId()))
                .isInstanceOf(BookingException.class);
    }

    @Test
    void approveBooking() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        BookingDto savedBooking = bookingService.createBooking(newBooking, user2.getId());
        BookingDto approvedBooking = bookingService.approveOrRejected(savedBooking.getId(), true, user.getId());
        assertThat(approvedBooking.getStatus()).usingRecursiveComparison().isEqualTo(StatusBooking.APPROVED);
    }

    @Test
    void rejectBooking() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        BookingDto savedBooking = bookingService.createBooking(newBooking, user2.getId());
        BookingDto approvedBooking = bookingService.approveOrRejected(savedBooking.getId(), false, user.getId());
        assertThat(approvedBooking.getStatus()).usingRecursiveComparison().isEqualTo(StatusBooking.REJECTED);
    }

    @Test
    void withNotExistingBooking() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        bookingService.createBooking(newBooking, user2.getId());
        assertThatThrownBy(() -> bookingService.approveOrRejected(user.getId(), true, 99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void approveBookingWhenBookingIsApprove() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        BookingDto savedBooking = bookingService.createBooking(newBooking, user2.getId());
        bookingService.approveOrRejected(savedBooking.getId(), true, user.getId());
        assertThatThrownBy(() -> bookingService.approveOrRejected(savedBooking.getId(), true,
                user.getId())).isInstanceOf(BookingException.class);
    }

    @Test
    void approveBookingWhenUserNotEqualsOwner() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        BookingDto savedBooking = bookingService.createBooking(newBooking, user2.getId());

        assertThatThrownBy(() -> bookingService.approveOrRejected(savedBooking.getId(), false,
                user2.getId())).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getBookingWhenBookingNotFound() {
        assertThatThrownBy(() -> bookingService.getBookingById(99L, user2.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getBookingByIdUserNotBookerOrOwner() {
        userRepository.save(user);
        userRepository.save(user2);
        userRepository.save(new User(3L, "user", "u@ya.ru"));
        itemRepository.save(item);
        BookingDto savedBooking = bookingService.createBooking(newBooking, user2.getId());

        assertThatThrownBy(() -> bookingService.getBookingById(savedBooking.getId(), 3L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getBookingWhenUserIsNotOwnerOrBooker() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        BookingDto savedBooking = bookingService.createBooking(newBooking, user2.getId());
        assertThatThrownBy(() -> bookingService.getBookingById(savedBooking.getId(), 10L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getAllBookingByUserId() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);
        Map<String, Long> addingBookings = addExtraBookings();
        List<Long> ids = bookingService
                .getAllBookingByUserId(PageRequest.of(0, 10), user2.getId(), "ALL")
                .stream().map(BookingDto::getId).collect(Collectors.toList());

        assertThat(ids).hasSize(8);

        assertThat(ids.get(0)).isEqualTo(addingBookings.get("futureBookingItem2"));
        assertThat(ids.get(1)).isEqualTo(addingBookings.get("futureBookingItem"));
        assertThat(ids.get(2)).isEqualTo(addingBookings.get("rejectedBookingItem2"));
        assertThat(ids.get(3)).isEqualTo(addingBookings.get("rejectedBookingItem"));
        assertThat(ids.get(4)).isEqualTo(addingBookings.get("waitingBookingItem2"));
        assertThat(ids.get(5)).isEqualTo(addingBookings.get("waitingBookingItem"));
        assertThat(ids.get(6)).isEqualTo(addingBookings.get("currentBookingItem2"));
        assertThat(ids.get(7)).isEqualTo(addingBookings.get("currentBookingItem"));
    }

    @Test
    void getBookingsOfOwner() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);
        Map<String, Long> addingBookings = addExtraBookings();
        List<Long> ids = bookingService
                .getBookingsOfOwner(PageRequest.of(0, 10), user.getId(), "ALL")
                .stream().map(BookingDto::getId)
                .collect(Collectors.toList());

        assertThat(ids).hasSize(4);

        assertThat(ids.get(0)).isEqualTo(addingBookings.get("futureBookingItem"));
        assertThat(ids.get(1)).isEqualTo(addingBookings.get("rejectedBookingItem"));
        assertThat(ids.get(2)).isEqualTo(addingBookings.get("waitingBookingItem"));
        assertThat(ids.get(3)).isEqualTo(addingBookings.get("currentBookingItem"));
    }


    @Test
    void tryCreatePastStart() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);
        addExtraBookings();
        List<Long> ids = bookingService
                .getBookingsOfOwner(PageRequest.of(0, 10), user.getId(), "CURRENT")
                .stream().map(BookingDto::getId)
                .collect(Collectors.toList());

        List<Long> ids2 = bookingService
                .getBookingsOfOwner(PageRequest.of(0, 10), user.getId(), "PAST")
                .stream().map(BookingDto::getId)
                .collect(Collectors.toList());

        assertThat(ids).isEmpty();
        assertThat(ids2).isEmpty();

        Booking tryCreatePastStart = new Booking();
        tryCreatePastStart.setStart(LocalDateTime.now().minusMinutes(1));
        tryCreatePastStart.setEnd(LocalDateTime.now().plusDays(1));
        tryCreatePastStart.setItem(item);
        tryCreatePastStart.setBooker(user2);
        tryCreatePastStart.setStatus(StatusBooking.APPROVED);
        assertThatThrownBy(() -> bookingRepository.save(tryCreatePastStart))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void getBookingsOfOwnerPast() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);
        Map<String, Long> addingBookings = addExtraBookings();

        List<Long> ids = bookingService
                .getBookingsOfOwner(PageRequest.of(0, 10), user.getId(), "PAST")
                .stream().map(BookingDto::getId)
                .collect(Collectors.toList());

        assertThat(ids).hasSize(0);
    }

    @Test
    void getBookingsOfOwnerFuture() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);
        Map<String, Long> addingBookings = addExtraBookings();

        List<Long> ids = bookingService
                .getBookingsOfOwner(PageRequest.of(0, 10), user.getId(), "FUTURE")
                .stream().map(BookingDto::getId)
                .collect(Collectors.toList());

        assertThat(ids).hasSize(4);

        assertThat(ids.get(0)).isEqualTo(addingBookings.get("futureBookingItem"));
        assertThat(ids.get(1)).isEqualTo(addingBookings.get("rejectedBookingItem"));
        assertThat(ids.get(2)).isEqualTo(addingBookings.get("waitingBookingItem"));
        assertThat(ids.get(3)).isEqualTo(addingBookings.get("currentBookingItem"));
    }

    @Test
    void getBookingsFuture() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);
        Map<String, Long> addingBookings = addExtraBookings();
        List<Long> ids = bookingService
                .getAllBookingByUserId(PageRequest.of(0, 10), user2.getId(), "FUTURE")
                .stream().map(BookingDto::getId).collect(Collectors.toList());

        assertThat(ids).hasSize(8);

        assertThat(ids.get(0)).isEqualTo(addingBookings.get("futureBookingItem2"));
        assertThat(ids.get(1)).isEqualTo(addingBookings.get("futureBookingItem"));
        assertThat(ids.get(2)).isEqualTo(addingBookings.get("rejectedBookingItem2"));
        assertThat(ids.get(3)).isEqualTo(addingBookings.get("rejectedBookingItem"));
        assertThat(ids.get(4)).isEqualTo(addingBookings.get("waitingBookingItem2"));
        assertThat(ids.get(5)).isEqualTo(addingBookings.get("waitingBookingItem"));
        assertThat(ids.get(6)).isEqualTo(addingBookings.get("currentBookingItem2"));
        assertThat(ids.get(7)).isEqualTo(addingBookings.get("currentBookingItem"));
    }

    @Test
    void getAllBookingsForUserWhenStateIsWaiting() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);
        Map<String, Long> addingBookings = addExtraBookings();
        List<Long> ids = bookingService
                .getAllBookingByUserId(PageRequest.of(0, 10), user2.getId(), "WAITING")
                .stream().map(BookingDto::getId).collect(Collectors.toList());

        assertThat(ids).hasSize(2);

        assertThat(ids.get(0)).isEqualTo(addingBookings.get("waitingBookingItem2"));
        assertThat(ids.get(1)).isEqualTo(addingBookings.get("waitingBookingItem"));
    }

    @Test
    void getAllBookingsForItemsUserWhenStateIsWaiting() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);
        Map<String, Long> addingBookings = addExtraBookings();
        List<Long> ids = bookingService
                .getBookingsOfOwner(PageRequest.of(0, 10), user2.getId(), "WAITING")
                .stream().map(BookingDto::getId).collect(Collectors.toList());

        assertThat(ids).hasSize(1);
        assertThat(ids.get(0)).isEqualTo(addingBookings.get("waitingBookingItem2"));
    }

    @Test
    void getAllBookingsUserWhenStateIsRejected() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);
        Map<String, Long> addingBookings = addExtraBookings();
        List<Long> ids = bookingService
                .getAllBookingByUserId(PageRequest.of(0, 10), user2.getId(), "rejected")
                .stream().map(BookingDto::getId).collect(Collectors.toList());

        assertThat(ids).hasSize(2);
        assertThat(ids.get(0)).isEqualTo(addingBookings.get("rejectedBookingItem2"));
        assertThat(ids.get(1)).isEqualTo(addingBookings.get("rejectedBookingItem"));
    }

    @Test
    void getAllBookingsForOwnerWhenStateIsRejected() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);
        Map<String, Long> addingBookings = addExtraBookings();
        List<Long> ids = bookingService
                .getBookingsOfOwner(PageRequest.of(0, 10), user2.getId(), "rejected")
                .stream().map(BookingDto::getId).collect(Collectors.toList());

        assertThat(ids).hasSize(1);
        assertThat(ids.get(0)).isEqualTo(addingBookings.get("rejectedBookingItem2"));
    }

    @Test
    void getAllBookingsUserWhenStateIsPast() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);
        List<Long> ids = bookingService
                .getAllBookingByUserId(PageRequest.of(0, 10), user2.getId(), "past")
                .stream().map(BookingDto::getId).collect(Collectors.toList());

        assertThat(ids).hasSize(0);
    }

    @Test
    void getAllBookingsUserWhenStateIsCurrent() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);
        List<Long> ids = bookingService
                .getAllBookingByUserId(PageRequest.of(0, 10), user2.getId(), "current")
                .stream().map(BookingDto::getId).collect(Collectors.toList());

        assertThat(ids).hasSize(0);
    }

    @Test
    void getBookingListWithUnknownState() {
        userRepository.save(user);
        assertThatThrownBy(() -> bookingService.getAllBookingByUserId(PageRequest.of(0, 10),
                user.getId(), "aaa")).isInstanceOf(BookingException.class);
    }

    @Test
    void getAllBookingsForUserWhenUserNotFound() {
        userRepository.save(user);
        assertThatThrownBy(() -> bookingService.getAllBookingByUserId(PageRequest.of(0, 10),
                50L, "ALL")).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getAllBookingsForItemsUserWhenUserNotFound() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);
        addExtraBookings();
        assertThatThrownBy(() -> bookingService.getBookingsOfOwner(PageRequest.of(0, 10),
                50L, "ALL")).isInstanceOf(NotFoundException.class);
    }

    private Map<String, Long> addExtraBookings() {
        Map<String, Long> ids = new HashMap<>();

        Booking currentBookingForItem = new Booking();
        currentBookingForItem.setStart(LocalDateTime.now().plusSeconds(5));
        currentBookingForItem.setEnd(LocalDateTime.now().plusDays(1));
        currentBookingForItem.setItem(item);
        currentBookingForItem.setBooker(user2);
        currentBookingForItem.setStatus(StatusBooking.APPROVED);
        ids.put("currentBookingItem", bookingRepository.save(currentBookingForItem).getId());

        Booking currentBookingForItem2 = new Booking();
        currentBookingForItem2.setStart(LocalDateTime.now().plusSeconds(5));
        currentBookingForItem2.setEnd(LocalDateTime.now().plusDays(1));
        currentBookingForItem2.setItem(item2);
        currentBookingForItem2.setBooker(user2);
        currentBookingForItem2.setStatus(StatusBooking.APPROVED);
        ids.put("currentBookingItem2", bookingRepository.save(currentBookingForItem2).getId());

        Booking futureBookingItem1 = new Booking();
        futureBookingItem1.setStart(LocalDateTime.now().plusDays(1));
        futureBookingItem1.setEnd(LocalDateTime.now().plusDays(2));
        futureBookingItem1.setItem(item);
        futureBookingItem1.setBooker(user2);
        futureBookingItem1.setStatus(StatusBooking.APPROVED);
        ids.put("futureBookingItem", bookingRepository.save(futureBookingItem1).getId());

        Booking futureBookingItem2 = new Booking();
        futureBookingItem2.setStart(LocalDateTime.now().plusDays(1));
        futureBookingItem2.setEnd(LocalDateTime.now().plusDays(2));
        futureBookingItem2.setItem(item2);
        futureBookingItem2.setBooker(user2);
        futureBookingItem2.setStatus(StatusBooking.APPROVED);
        ids.put("futureBookingItem2", bookingRepository.save(futureBookingItem2).getId());

        Booking waitingBookingItem1 = new Booking();
        waitingBookingItem1.setStart(LocalDateTime.now().plusHours(1));
        waitingBookingItem1.setEnd(LocalDateTime.now().plusHours(2));
        waitingBookingItem1.setItem(item);
        waitingBookingItem1.setBooker(user2);
        waitingBookingItem1.setStatus(StatusBooking.WAITING);
        ids.put("waitingBookingItem", bookingRepository.save(waitingBookingItem1).getId());

        Booking waitingBookingItem2 = new Booking();
        waitingBookingItem2.setStart(LocalDateTime.now().plusHours(1));
        waitingBookingItem2.setEnd(LocalDateTime.now().plusHours(2));
        waitingBookingItem2.setItem(item2);
        waitingBookingItem2.setBooker(user2);
        waitingBookingItem2.setStatus(StatusBooking.WAITING);
        ids.put("waitingBookingItem2", bookingRepository.save(waitingBookingItem2).getId());

        Booking rejectedBookingItem1 = new Booking();
        rejectedBookingItem1.setStart(LocalDateTime.now().plusHours(1));
        rejectedBookingItem1.setEnd(LocalDateTime.now().plusHours(2));
        rejectedBookingItem1.setItem(item);
        rejectedBookingItem1.setBooker(user2);
        rejectedBookingItem1.setStatus(StatusBooking.REJECTED);
        ids.put("rejectedBookingItem", bookingRepository.save(rejectedBookingItem1).getId());

        Booking rejectedBookingItem2 = new Booking();
        rejectedBookingItem2.setStart(LocalDateTime.now().plusHours(1));
        rejectedBookingItem2.setEnd(LocalDateTime.now().plusHours(2));
        rejectedBookingItem2.setItem(item2);
        rejectedBookingItem2.setBooker(user2);
        rejectedBookingItem2.setStatus(StatusBooking.REJECTED);
        ids.put("rejectedBookingItem2", bookingRepository.save(rejectedBookingItem2).getId());

        return ids;
    }
}
