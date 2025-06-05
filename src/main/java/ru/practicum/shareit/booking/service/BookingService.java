package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingRequest;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
        BookingDto createBooking(BookingRequest bookingRequest, Long userId);

        BookingDto approveBooking(Long bookingId, Long ownerId, boolean approved);

        BookingDto getBookingById(Long bookingId, Long userId);

        List<BookingDto> getUserBookings(Long userId, BookingState state);

        List<BookingDto> getOwnerBookings(Long ownerId, BookingState state);
}