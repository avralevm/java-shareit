package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestCreate;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(ItemRequestCreate itemRequestCreate, Long userId);

    List<ItemRequestDto> getUserItemRequests(Long userId);

    List<ItemRequestDto> getOtherUsersItemRequests(Long userId);

    ItemRequestDto getItemRequestById(Long requestId, Long userId);
}