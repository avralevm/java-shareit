package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestCreate;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    @Mapping(source = "requestor.id", target = "requestorId")
    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    ItemRequest toItemRequest(ItemRequestCreate itemRequestCreate);
}