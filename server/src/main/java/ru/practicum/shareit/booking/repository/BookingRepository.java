package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long userId);

    @Query("""
             SELECT b FROM Booking b
             WHERE b.booker.id = :userId
             AND b.start <= CURRENT_TIMESTAMP
             AND b.end >= CURRENT_TIMESTAMP
             AND b.status = APPROVED
             ORDER BY b.start DESC
             """)
    List<Booking> findCurrentBookingsByUser(@Param("userId") Long userId);

    @Query("""
             SELECT b FROM Booking b
             WHERE b.booker.id = :userId
             AND b.status = WAITING
             ORDER BY b.start DESC
             """)
    List<Booking> findWaitingBookingsByUser(@Param("userId") Long userId);

    @Query("""
             SELECT b FROM Booking b
             WHERE b.booker.id = :userId
             AND b.end < CURRENT_TIMESTAMP
             ORDER BY b.start DESC
             """)
    List<Booking> findPastBookingsByUser(@Param("userId") Long userId);

    @Query("""
             SELECT b FROM Booking b
             WHERE b.booker.id = :userId
             AND b.status = REJECTED
             ORDER BY b.start DESC
             """)
    List<Booking> findRejectedBookingsByUser(@Param("userId") Long userId);

    @Query("""
             SELECT b FROM Booking b
             WHERE b.booker.id = :userId
             AND b.start > CURRENT_TIMESTAMP
             ORDER BY b.start DESC
             """)
    List<Booking> findFutureBookingsByUser(@Param("userId") Long userId);

    @Query("""
             SELECT b FROM Booking b
             WHERE b.item.id = :itemId
             AND b.status = APPROVED
             AND b.end <= CURRENT_TIMESTAMP
             ORDER BY b.end DESC
             LIMIT 1
             """)
    Booking findLastBooking(@Param("itemId") Long itemId);

    @Query("""
             SELECT b FROM Booking b
             WHERE b.item.id = :itemId
             AND b.status = APPROVED
             AND b.start > CURRENT_TIMESTAMP
             ORDER BY b.end DESC
             LIMIT 1
             """)
    Booking findNextBooking(@Param("itemId") Long itemId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.owner.id = :ownerId " +
            "AND b.start <= CURRENT_TIMESTAMP " +
            "AND b.end >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsByOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.owner.id = :ownerId " +
            "AND b.status = 'WAITING' " +
            "ORDER BY b.start DESC")
    List<Booking> findWaitingBookingsByOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.owner.id = :ownerId " +
            "AND b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findPastBookingsByOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.owner.id = :ownerId " +
            "AND b.status = 'REJECTED' " +
            "ORDER BY b.start DESC")
    List<Booking> findRejectedBookingsByOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.owner.id = :ownerId " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureBookingsByOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.owner.id = :ownerId " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(@Param("ownerId") Long ownerId);
}