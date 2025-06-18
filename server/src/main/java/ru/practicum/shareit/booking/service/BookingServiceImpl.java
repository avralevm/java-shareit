package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingRequest;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto createBooking(BookingRequest bookingRequest, Long userId) {
        Long itemId = bookingRequest.getItemId();
        Item item = findItemOrThrow(itemId);
        User owner = item.getOwner();
        User booker = findUserOrThrow(userId);

        if (owner.equals(booker)) {
            throw new ValidationException("Владелец не может бронировать свою вещь ");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        LocalDateTime start = bookingRequest.getStart();
        LocalDateTime end = bookingRequest.getEnd();

        if (start.isAfter(end) || start.isEqual(end) || start.isBefore(LocalDateTime.now())
                || end.isBefore(LocalDateTime.now())) {
            throw new ValidationException(String.format("Некорректные даты бронирования. Начало: {} Конец: {}", start, end));
        }

        Booking booking = bookingMapper.toBooking(bookingRequest);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking createdBooking = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(createdBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long bookingId, Long userId) {
        findUserOrThrow(userId);
        Booking booking = findBookingOrThrow(bookingId);
        User owner = booking.getItem().getOwner();
        User booker = booking.getBooker();

        if (!booker.getId().equals(userId) && !owner.getId().equals(userId)) {
            throw new ValidationException("Просмотр бронирования доступен только автору или владельцу вещи");
        }

        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto approveBooking(Long bookingId, Long ownerId, boolean approved) {
        Booking booking = findBookingOrThrow(bookingId);

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ValidationException("Подтверждать бронирование может только владелец вещи");
        }

        findUserOrThrow(ownerId);

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        Booking approvedBooking = bookingRepository.save(booking);
        log.info("Статус бронирования изменен: {}", approvedBooking);
        return bookingMapper.toBookingDto(approvedBooking);
    }


    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getUserBookings(Long userId, BookingState state) {
        findUserOrThrow(userId);

        List<Booking> userBookings = switch (state) {
            case CURRENT -> bookingRepository.findCurrentBookingsByUser(userId);
            case WAITING -> bookingRepository.findWaitingBookingsByUser(userId);
            case PAST -> bookingRepository.findPastBookingsByUser(userId);
            case REJECTED -> bookingRepository.findRejectedBookingsByUser(userId);
            case FUTURE -> bookingRepository.findFutureBookingsByUser(userId);
            default -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
        };

        return userBookings.stream()
                .map(bookingMapper::toBookingDto)
                .toList();
    }

      @Override
      @Transactional(readOnly = true)
      public List<BookingDto> getOwnerBookings(Long ownerId, BookingState state) {
          findUserOrThrow(ownerId);
          List<Item> userItem = itemRepository.findAllByOwnerId(ownerId);

          if (userItem.isEmpty()) {
              throw  new NotFoundException(String.format("Предметы пользователя с id = %d не найдены", ownerId));
          }

          List<Booking> userBookings = switch (state) {
              case CURRENT -> bookingRepository.findCurrentBookingsByUser(ownerId);
              case WAITING -> bookingRepository.findWaitingBookingsByUser(ownerId);
              case PAST -> bookingRepository.findPastBookingsByUser(ownerId);
              case REJECTED -> bookingRepository.findRejectedBookingsByUser(ownerId);
              case FUTURE -> bookingRepository.findFutureBookingsByUser(ownerId);
              default -> bookingRepository.findByBookerIdOrderByStartDesc(ownerId);
          };

          return userBookings.stream()
                  .map(bookingMapper::toBookingDto)
                  .toList();
      }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с id {} не найден", userId);
                    return new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
                });
    }

    private Item findItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Предмет с id {} не найден", itemId);
                    return new NotFoundException(String.format("Предмет с id = %d не найден", itemId));
                });
    }

    private Booking findBookingOrThrow(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.error("Бронирование с id {} не найдено", bookingId);
                    return new NotFoundException(String.format("Бронирование с id = %d не найдено", bookingId));
                });
    }
}
