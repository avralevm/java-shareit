package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "request.id", target = "requestId")
    ItemDto toItemDto(Item item);

    Item toItem(ItemDto itemDto);

    ItemOwnerDto toItemOwnerDto(Item item);

    List<ItemDto> toItemsDto(List<Item> items);
}