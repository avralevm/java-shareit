package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingRequest;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingRequest bookingRequest,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на бронирование: {} от пользователя с id {}", bookingRequest, userId);
        return bookingService.createBooking(bookingRequest, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение информации о бронировании");
        return bookingService.getBookingById(bookingId, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId, @RequestParam Boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Запрос на изменение статуса бронирования");
        return bookingService.approveBooking(bookingId, ownerId, approved);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Запрос на получение всех бронирований пользователя с id {} со статусом {}.", userId, state);
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Запрос на получение всех бронирований владельца с id {} со статусом {}.", ownerId, state);
        return bookingService.getOwnerBookings(ownerId, state);
    }
}