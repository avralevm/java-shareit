package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.booking.dto.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({ItemServiceImpl.class, UserServiceImpl.class, ItemMapperImpl.class, UserMapperImpl.class,
        CommentMapperImpl.class, BookingMapperImpl.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemServiceImplTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    private User savedUser;
    private Item savedItem;

    @BeforeEach
    void setUp() {
        savedUser = userRepository.save(new User(null, "Test User", "test@example.com"));
        savedItem = itemRepository.save(
                Item.builder()
                        .name("Test Item")
                        .description("Test Description")
                        .available(true)
                        .owner(savedUser)
                        .build()
        );
    }

    @Test
    void createItemShouldSaveAndReturnItem() {
        ItemDto itemDto = ItemDto.builder()
                .name("New Item")
                .description("New Description")
                .available(true)
                .build();

        ItemDto result = itemService.createItem(itemDto, savedUser.getId());

        assertNotNull(result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertTrue(itemRepository.existsById(result.getId()));
    }

    @Test
    void createItemShouldNotSetRequestWhenRequestIdNotProvided() {
        ItemDto itemDto = ItemDto.builder()
                .name("Item without request")
                .description("Description")
                .available(true)
                .build();

        ItemDto result = itemService.createItem(itemDto, savedUser.getId());

        assertNotNull(result);
        assertNull(result.getRequestId());
    }

    @Test
    void createItemShouldIgnoreNonExistentRequestId() {
        Long nonExistentRequestId = 999L;

        ItemDto itemDto = ItemDto.builder()
                .name("Item with non-existent request")
                .description("Description")
                .available(true)
                .requestId(nonExistentRequestId)
                .build();

        ItemDto result = itemService.createItem(itemDto, savedUser.getId());

        assertNotNull(result);
        assertNull(result.getRequestId());
    }

    @Test
    void getItemByIdShouldReturnItem() {
        ItemOwnerDto result = itemService.getItemById(savedItem.getId(), savedUser.getId());

        assertEquals(savedItem.getId(), result.getId());
        assertEquals(savedItem.getName(), result.getName());
    }

    @Test
    void updateItemShouldUpdateFields() {
        ItemDto updateDto = ItemDto.builder()
                .name("Updated Name")
                .description(null)
                .available(false)
                .build();

        ItemDto result = itemService.updateItem(updateDto, savedUser.getId(), savedItem.getId());

        assertEquals("Updated Name", result.getName());
        assertEquals(savedItem.getDescription(), result.getDescription());
        assertFalse(result.getAvailable());
    }

    @Test
    void deleteItemShouldRemoveFromDb() {
        itemService.deleteItem(savedItem.getId());

        assertFalse(itemRepository.existsById(savedItem.getId()));
    }

    @Test
    void deleteItemShouldThrowWhenItemNotFound() {
        Long nonExistentItemId = 999L;

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.deleteItem(nonExistentItemId));

        assertEquals(String.format("Предмет с id = %d не найдена", nonExistentItemId),
                exception.getMessage());
    }

    @Test
    void getOwnerItemsShouldReturnItemsForOwner() {
        List<ItemOwnerDto> result = itemService.getOwnerItems(savedUser.getId());

        assertFalse(result.isEmpty());
        assertEquals(savedItem.getId(), result.get(0).getId());
    }

    @Test
    void getOwnerItemsShouldThrowWhenNoItemsFound() {
        User userWithoutItems = userRepository.save(new User(null, "No Items", "noitems@example.com"));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getOwnerItems(userWithoutItems.getId()));

        assertEquals(String.format("Предметы владельца с id = %d не найдены", userWithoutItems.getId()),
                exception.getMessage());
    }

    @Test
    void searchItemsByTextShouldReturnMatchingItems() {
        List<ItemDto> result = itemService.searchItemsByText("Test");

        assertFalse(result.isEmpty());
        assertEquals(savedItem.getName(), result.get(0).getName());
    }

    @Test
    void searchItemsByTextShouldReturnEmptyListWhenTextIsEmpty() {
        List<ItemDto> resultEmpty = itemService.searchItemsByText("");
        List<ItemDto> resultNull = itemService.searchItemsByText(null);

        assertTrue(resultEmpty.isEmpty());
        assertTrue(resultNull.isEmpty());
    }

    @Test
    void createCommentShouldSaveAndReturnComment() {
        User booker = userRepository.save(new User(null, "Booker", "booker@example.com"));
        Booking booking = bookingRepository.save(
                Booking.builder()
                        .start(LocalDateTime.now().minusDays(2))
                        .end(LocalDateTime.now().minusDays(1))
                        .item(savedItem)
                        .booker(booker)
                        .status(BookingStatus.APPROVED)
                        .build()
        );

        CommentDto commentDto = CommentDto.builder()
                .text("Test Comment")
                .build();

        CommentDto result = itemService.createComment(savedItem.getId(), commentDto, booker.getId());

        assertNotNull(result.getId());
        assertEquals("Test Comment", result.getText());
        assertEquals("Booker", result.getAuthorName());
    }

    @Test
    void createCommentShouldThrowWhenNoCompletedBookings() {
        CommentDto commentDto = CommentDto.builder()
                .text("Test Comment")
                .build();

        assertThrows(BadRequestException.class, () ->
                itemService.createComment(savedItem.getId(), commentDto, savedUser.getId()));
    }

    @Test
    void getItemByIdShouldIncludeBookingsForOwner() {
        // Создаем тестовые бронирования
        User booker = userRepository.save(new User(null, "Booker", "booker@example.com"));
        Booking pastBooking = bookingRepository.save(
                Booking.builder()
                        .start(LocalDateTime.now().minusDays(2))
                        .end(LocalDateTime.now().minusDays(1))
                        .item(savedItem)
                        .booker(booker)
                        .status(BookingStatus.APPROVED)
                        .build()
        );
        Booking futureBooking = bookingRepository.save(
                Booking.builder()
                        .start(LocalDateTime.now().plusDays(1))
                        .end(LocalDateTime.now().plusDays(2))
                        .item(savedItem)
                        .booker(booker)
                        .status(BookingStatus.APPROVED)
                        .build()
        );

        ItemOwnerDto result = itemService.getItemById(savedItem.getId(), savedUser.getId());

        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
        assertEquals(pastBooking.getId(), result.getLastBooking().getId());
        assertEquals(futureBooking.getId(), result.getNextBooking().getId());
    }
}