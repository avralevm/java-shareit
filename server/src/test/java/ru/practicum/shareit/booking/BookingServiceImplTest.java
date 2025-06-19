package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingRequest;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentMapperImpl;
import ru.practicum.shareit.item.dto.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({BookingServiceImpl.class, UserServiceImpl.class, ItemServiceImpl.class, BookingMapperImpl.class,
        UserMapperImpl.class, ItemMapperImpl.class, CommentMapperImpl.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookingServiceImplTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User(null, "Owner", "owner@example.com"));
        booker = userRepository.save(new User(null, "Booker", "booker@example.com"));

        item = itemRepository.save(
                Item.builder()
                        .name("Test Item")
                        .description("Test Description")
                        .available(true)
                        .owner(owner)
                        .build()
        );

        booking = bookingRepository.save(
                Booking.builder()
                        .start(LocalDateTime.now().plusHours(1))
                        .end(LocalDateTime.now().plusHours(2))
                        .item(item)
                        .booker(booker)
                        .status(BookingStatus.WAITING)
                        .build()
        );
    }

    @Test
    void createBookingShouldSaveAndReturnBooking() {
        BookingRequest request = BookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingDto result = bookingService.createBooking(request, booker.getId());

        assertNotNull(result.getId());
        assertEquals(request.getItemId(), result.getItem().getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
        assertTrue(bookingRepository.existsById(result.getId()));
    }

    @Test
    void getBookingByIdShouldReturnBooking() {
        BookingDto result = bookingService.getBookingById(booking.getId(), booker.getId());

        assertEquals(booking.getId(), result.getId());
        assertEquals(item.getId(), result.getItem().getId());
    }

    @Test
    void approveBookingShouldUpdateStatus() {
        BookingDto result = bookingService.approveBooking(booking.getId(), owner.getId(), true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        assertEquals(BookingStatus.APPROVED, bookingRepository.findById(booking.getId()).get().getStatus());
    }

    @Test
    void getUserBookingsShouldReturnUserBookings() {
        List<BookingDto> result = bookingService.getUserBookings(booker.getId(), BookingState.ALL);

        assertFalse(result.isEmpty());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void createBookingShouldThrowWhenBookingOwnItem() {
        BookingRequest request = BookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(request, owner.getId()));
    }

    @Test
    void createBookingShouldThrowWhenItemNotAvailable() {
        item.setAvailable(false);
        itemRepository.save(item);

        BookingRequest request = BookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(request, booker.getId()));
    }

    @Test
    void getBookingByIdShouldThrowWhenNotAuthorized() {
        User anotherUser = userRepository.save(new User(null, "Another", "another@example.com"));

        assertThrows(ValidationException.class,
                () -> bookingService.getBookingById(booking.getId(), anotherUser.getId()));
    }

    @Test
    void approveBookingShouldThrowWhenNotOwner() {
        assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(booking.getId(), booker.getId(), true));
    }

    @Test
    void createBookingShouldThrowWhenStartAfterEnd() {
        BookingRequest request = BookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(request, booker.getId()));
    }

    @Test
    void createBookingShouldThrowWhenStartEqualsEnd() {
        LocalDateTime now = LocalDateTime.now();
        BookingRequest request = BookingRequest.builder()
                .itemId(item.getId())
                .start(now)
                .end(now)
                .build();

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(request, booker.getId()));
    }

    @Test
    void createBookingShouldThrowWhenStartInPast() {
        BookingRequest request = BookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(request, booker.getId()));
    }

    @Test
    void createBookingShouldThrowWhenEndInPast() {
        BookingRequest request = BookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(request, booker.getId()));
    }

    @Test
    void approveBookingShouldSetRejectedStatus() {
        BookingDto result = bookingService.approveBooking(booking.getId(), owner.getId(), false);

        assertEquals(BookingStatus.REJECTED, result.getStatus());
        assertEquals(BookingStatus.REJECTED, bookingRepository.findById(booking.getId()).get().getStatus());
    }

    @Test
    void getUserBookingsShouldReturnCurrentBookings() {
        Booking currentBooking = bookingRepository.save(
                Booking.builder()
                        .start(LocalDateTime.now().minusHours(1))
                        .end(LocalDateTime.now().plusHours(1))
                        .item(item)
                        .booker(booker)
                        .status(BookingStatus.APPROVED)
                        .build()
        );

        List<BookingDto> result = bookingService.getUserBookings(booker.getId(), BookingState.CURRENT);

        assertEquals(1, result.size());
        assertEquals(currentBooking.getId(), result.get(0).getId());
    }

    @Test
    void getUserBookingsShouldReturnWaitingBookings() {
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);

        List<BookingDto> result = bookingService.getUserBookings(booker.getId(), BookingState.WAITING);

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void getUserBookingsShouldReturnPastBookings() {
        Booking pastBooking = bookingRepository.save(
                Booking.builder()
                        .start(LocalDateTime.now().minusDays(2))
                        .end(LocalDateTime.now().minusDays(1))
                        .item(item)
                        .booker(booker)
                        .status(BookingStatus.APPROVED)
                        .build()
        );

        List<BookingDto> result = bookingService.getUserBookings(booker.getId(), BookingState.PAST);

        assertEquals(1, result.size());
        assertEquals(pastBooking.getId(), result.get(0).getId());
    }

    @Test
    void getUserBookingsShouldReturnRejectedBookings() {
        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);

        List<BookingDto> result = bookingService.getUserBookings(booker.getId(), BookingState.REJECTED);

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void getOwnerBookingsShouldReturnOwnerBookings() {
        List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), BookingState.ALL);

        assertFalse(result.isEmpty());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void getOwnerBookingsShouldReturnCurrentBookings() {
        Booking currentBooking = bookingRepository.save(
                Booking.builder()
                        .start(LocalDateTime.now().minusHours(1))
                        .end(LocalDateTime.now().plusHours(1))
                        .item(item)
                        .booker(booker)
                        .status(BookingStatus.APPROVED)
                        .build()
        );

        List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), BookingState.CURRENT);

        assertEquals(1, result.size());
        assertEquals(currentBooking.getId(), result.get(0).getId());
    }

    @Test
    void getOwnerBookingsShouldReturnWaitingBookings() {
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);

        List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), BookingState.WAITING);

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void getOwnerBookingsShouldReturnPastBookings() {
        Booking pastBooking = bookingRepository.save(
                Booking.builder()
                        .start(LocalDateTime.now().minusDays(2))
                        .end(LocalDateTime.now().minusDays(1))
                        .item(item)
                        .booker(booker)
                        .status(BookingStatus.APPROVED)
                        .build()
        );

        List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), BookingState.PAST);

        assertEquals(1, result.size());
        assertEquals(pastBooking.getId(), result.get(0).getId());
    }

    @Test
    void getOwnerBookingsShouldReturnRejectedBookings() {
        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);

        List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), BookingState.REJECTED);

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void getOwnerBookingsShouldReturnFutureBookings() {
        List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), BookingState.FUTURE);

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void getOwnerBookingsShouldThrowWhenNoItems() {
        User userWithoutItems = userRepository.save(new User(null, "No Items", "noitems@example.com"));

        assertThrows(NotFoundException.class,
                () -> bookingService.getOwnerBookings(userWithoutItems.getId(), BookingState.ALL));
    }

    @Test
    void findUserOrThrowShouldLogErrorWhenUserNotFound() {
        Long nonExistentUserId = 999L;

        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(1L, nonExistentUserId));
    }

    @Test
    void findBookingOrThrowShouldLogErrorWhenBookingNotFound() {
        Long nonExistentBookingId = 999L;

        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(nonExistentBookingId, booker.getId()));
    }

    @Test
    void findItemOrThrowShouldLogErrorWhenItemNotFound() {
        Long nonExistentItemId = 999L;
        BookingRequest request = BookingRequest.builder()
                .itemId(nonExistentItemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(request, booker.getId()));
    }
}