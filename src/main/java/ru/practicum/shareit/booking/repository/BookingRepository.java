package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdAndStatus(Long userId, StatusBooking state, Sort sort);

    List<Booking> findAllByBookerIdAndEndBefore(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByBookerIdAndStartAfter(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByBookerId(Long userId, Sort sort);

    @Query(value = "select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2")
    List<Booking> findAllByBookerIdAndStartAfterAndEndBefore(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByItemOwnerAndStatus(Long id, StatusBooking waiting, Sort sortByStartDesc);

    List<Booking> findAllByItemOwnerAndEndBefore(Long owner, LocalDateTime now, Sort sortByStartDesc);

    List<Booking> findAllByItemOwner(Long ownerId, Sort sortByStartDesc);

    @Query(value = "select b from Booking b where b.item.owner = ?1 and b.start < ?2 and b.end > ?2 ")
    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime now, Sort sortByStartDesc);

    List<Booking> findAllByItemOwnerAndStartAfter(Long ownerId, LocalDateTime now, Sort sortByStartDesc);

    List<Booking> findByItemIdAndStatus(Long itemId, StatusBooking approved, Sort start);

    List<Booking> findAllByItemIdAndBookerIdAndStatus(long itemId, long userId, StatusBooking approved, Sort start);

    @Query(value = "select b from Booking b where b.item.owner = ?1 and b.status = 'APPROVED'")
    List<Booking> findAllByItemOwnerWhereStatusApproved(Long userId, Sort start);
}
