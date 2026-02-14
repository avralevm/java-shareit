package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestCreate;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({ItemRequestServiceImpl.class, ItemRequestMapperImpl.class, ItemMapperImpl.class, UserMapperImpl.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemRequestServiceImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRequestService itemRequestService;

    private User testUser;
    private Item testItem;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(new User(null, "Test User", "test@example.com"));
    }

    @Test
    void createItemRequestShouldSaveAndReturnRequest() {
        ItemRequestCreate requestCreate = new ItemRequestCreate();
        requestCreate.setDescription("Need a drill");

        ItemRequestDto result = itemRequestService.createItemRequest(requestCreate, testUser.getId());

        assertNotNull(result.getId());
        assertEquals(requestCreate.getDescription(), result.getDescription());
        assertEquals(testUser.getId(), result.getRequestorId());
        assertTrue(itemRequestRepository.existsById(result.getId()));
    }

    @Test
    void getUserItemRequestsShouldReturnUserRequests() {
        ItemRequest request = new ItemRequest();
        request.setDescription("Test request");
        request.setRequestor(testUser);
        request.setCreated(LocalDateTime.now());
        itemRequestRepository.save(request);

        List<ItemRequestDto> result = itemRequestService.getUserItemRequests(testUser.getId());

        assertEquals(1, result.size());
        assertEquals(request.getDescription(), result.get(0).getDescription());
    }

    @Test
    void getOtherUsersItemRequestsShouldNotReturnCurrentUserRequests() {
        User anotherUser = userRepository.save(new User(null, "Another User", "another@example.com"));

        // Запрос текущего пользователя
        ItemRequest userRequest = new ItemRequest();
        userRequest.setRequestor(testUser);
        userRequest.setDescription("User request");
        userRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(userRequest);

        // Запрос другого пользователя
        ItemRequest anotherRequest = new ItemRequest();
        anotherRequest.setRequestor(anotherUser);
        anotherRequest.setDescription("Another request");
        anotherRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(anotherRequest);

        List<ItemRequestDto> result = itemRequestService.getOtherUsersItemRequests(testUser.getId());

        assertEquals(1, result.size());
        assertEquals(anotherRequest.getDescription(), result.get(0).getDescription());
    }

    @Test
    void getItemRequestByIdShouldReturnRequestWithItems() {
        ItemRequest request = new ItemRequest();
        request.setDescription("Test request");
        request.setRequestor(testUser);
        request.setCreated(LocalDateTime.now());
        ItemRequest savedRequest = itemRequestRepository.save(request);

        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(testUser);
        item.setRequest(savedRequest);
        itemRepository.save(item);

        ItemRequestDto result = itemRequestService.getItemRequestById(savedRequest.getId(), testUser.getId());

        assertNotNull(result);
        assertEquals(savedRequest.getDescription(), result.getDescription());
        assertEquals(1, result.getItems().size());
        assertEquals(item.getName(), result.getItems().get(0).getName());
    }

    @Test
    void getItemRequestByIdWhenNotFoundShouldThrowException() {
        assertThrows(NotFoundException.class, () ->
                itemRequestService.getItemRequestById(999L, testUser.getId()));
    }

    @Test
    void createItemRequestWithNonExistentUserShouldThrowException() {
        ItemRequestCreate requestCreate = new ItemRequestCreate();
        requestCreate.setDescription("Need help");

        assertThrows(NotFoundException.class, () ->
                itemRequestService.createItemRequest(requestCreate, 999L));
    }
}