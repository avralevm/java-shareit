package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestCreate;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestDto createItemRequest(ItemRequestCreate itemRequestCreate, Long userId) {
        User requester = findUserOrThrow(userId);

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestCreate);

        itemRequest.setRequestor(requester);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest createdItemRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestDto(createdItemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getUserItemRequests(Long userId) {
        findUserOrThrow(userId);

        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId);

        return requests.stream()
                .map(request -> {
                    ItemRequestDto dto = itemRequestMapper.toItemRequestDto(request);
                    dto.setItems(getItemsForRequest(request.getId()));
                    return dto;
                }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getOtherUsersItemRequests(Long userId) {
        findUserOrThrow(userId);

        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId);

        return requests.stream()
                .map(request -> {
                    ItemRequestDto dto = itemRequestMapper.toItemRequestDto(request);
                    dto.setItems(getItemsForRequest(request.getId()));
                    return dto;
                }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getItemRequestById(Long requestId, Long userId) {
        findUserOrThrow(userId);
        ItemRequest itemRequest = findItemRequestOrThrow(requestId);

        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(getItemsForRequest(requestId));
        return itemRequestDto;
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с id {} не найден", userId);
                    return new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
                });
    }

    private ItemRequest findItemRequestOrThrow(long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.error("Запрос с id {} на добавление вещи не найден", requestId);
                    return new NotFoundException(String.format("Запрос с id = %d на добавление вещи не найден",
                            requestId));
                });
    }

    private List<ItemDto> getItemsForRequest(Long requestId) {
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        return items.stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}