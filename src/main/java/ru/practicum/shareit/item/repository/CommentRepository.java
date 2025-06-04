package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItemId(Long itemId);

    @Query("SELECT c FROM Comment c WHERE c.item.id IN :itemIds")
    List<Comment> findAllByItemIds(@Param("itemIds") List<Long> itemIds);

    @Query("SELECT COUNT(b) > 0" +
            " FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.booker.id = :userId " +
            "AND b.status = APPROVED " +
            "AND b.end < CURRENT_TIMESTAMP")
    boolean existsApprovedPastBookingForUserAndItem(@Param("userId") Long userId, @Param("itemId") Long itemId);
}