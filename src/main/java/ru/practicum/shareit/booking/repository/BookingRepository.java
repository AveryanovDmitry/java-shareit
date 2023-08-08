package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends PagingAndSortingRepository<Booking, Long> {
    List<Booking> findAllByBookerIdAndStatus(PageRequest pageable, Long userId, StatusBooking state, Sort sort);

    List<Booking> findAllByBookerIdAndEndBefore(PageRequest pageable, Long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByBookerIdAndStartAfter(PageRequest pageable, Long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByBookerId(PageRequest pageable, Long userId, Sort sort);

    @Query(value = "select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2")
    List<Booking> findAllByBookerIdAndStartAfterAndEndBefore(PageRequest pageable,
                                                             Long userId, LocalDateTime now, Sort sort);
    List<Booking> findAllByItemOwnerAndStatus(PageRequest pageable, Long id, StatusBooking waiting, Sort sortByStartDesc);

    List<Booking> findAllByItemOwnerAndEndBefore(PageRequest pageable, Long owner, LocalDateTime now, Sort sortByStartDesc);

    List<Booking> findAllByItemOwner(PageRequest pageable, Long ownerId, Sort sortByStartDesc);

    @Query(value = "select b from Booking b where b.item.owner = ?1 and b.start < ?2 and b.end > ?2 ")
    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(PageRequest pageable,
                                                              Long ownerId, LocalDateTime now, Sort sortByStartDesc);

    List<Booking> findAllByItemOwnerAndStartAfter(PageRequest pageable,
                                                  Long ownerId, LocalDateTime now, Sort sortByStartDesc);

    @Query(value = "select b from Booking b where b.item.id = ?1 and b.status = 'APPROVED'")
    List<Booking> findByItemIdAndStatus(Long itemId, Sort start);

    List<Booking> findAllByItemIdAndBookerIdAndStatus(long itemId, long userId, StatusBooking approved, Sort start);

    @Query(value = "select b from Booking b where b.item.owner = ?1 and b.status = 'APPROVED'")
    List<Booking> findAllByItemOwnerWhereStatusApproved(Long userId, Sort start);
}
