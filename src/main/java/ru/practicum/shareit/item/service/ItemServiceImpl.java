package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ItemOwnerDto> getOwnerItems(Long userId) {
        userService.getUserById(userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId);

        if (items.isEmpty()) {
            throw new NotFoundException(String.format("Предметы владельца с id = %d не найдены", userId));
        }

        List<Long> itemIds = items.stream().map(Item::getId).toList();
        Map<Long, List<Comment>> commentsByItem = commentRepository.findAllByItemIds(itemIds)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        return items.stream()
                .map(item -> {
                    ItemOwnerDto itemOwnerDto = itemMapper.toItemOwnerDto(item);

                    Booking lastBooking = bookingRepository.findLastBooking(item.getId());
                    if (lastBooking != null) {
                        itemOwnerDto.setLastBooking(bookingMapper.toBookingDto(lastBooking));
                    }

                    Booking nextBooking = bookingRepository.findNextBooking(item.getId());
                    if (nextBooking != null) {
                        itemOwnerDto.setNextBooking(bookingMapper.toBookingDto(nextBooking));
                    }

                    List<Comment> comments = commentsByItem.getOrDefault(item.getId(), Collections.emptyList());
                    itemOwnerDto.setComments(comments.stream()
                            .map(commentMapper::toCommentDto)
                            .collect(Collectors.toList()));

                    return itemOwnerDto;
                }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(Long id) {
        Item item = findItemOrThrow(id);
        ItemDto itemDto = itemMapper.toItemDto(item);

        List<Comment> comments = commentRepository.findAllByItemId(id);
        itemDto.setComments(comments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList()));

        return itemDto;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        UserDto ownerDto = userService.getUserById(userId);
        User owner = userMapper.toUser(ownerDto);
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(owner);
        Item createdItem = itemRepository.save(item);
        return itemMapper.toItemDto(createdItem);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId) {
        Item item = findItemOrThrow(itemId);
        UserDto ownerDto = userService.getUserById(userId);
        User owner = userMapper.toUser(ownerDto);
        item.setOwner(owner);

        Optional.ofNullable(itemDto.getName()).ifPresent(item::setName);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(item::setAvailable);

        Item updatedItem = itemRepository.save(item);
        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new NotFoundException(String.format("Предмет с id = %d не найдена", id));
        }
        itemRepository.deleteById(id);
        log.info("Предмет с id {} был удален", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItemsByText(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItemsByText(text).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public CommentDto createComment(Long itemId, CommentDto commentDto, Long userId) {
        Item item = findItemOrThrow(itemId);
        UserDto authorDto = userService.getUserById(userId);
        User author = userMapper.toUser(authorDto);

        if (!commentRepository.existsApprovedPastBookingForUserAndItem(userId, itemId)) {
            throw new BadRequestException("Пользователь не может оставить отзыв на эту вещь");
        }

        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        Comment savedComment = commentRepository.save(comment);
        CommentDto result = commentMapper.toCommentDto(savedComment);
        result.setAuthorName(author.getName());

        return result;
    }


    private Item findItemOrThrow(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Предмет с id {} не найден", id);
                    return new NotFoundException(String.format("Предмет с id = %d не найден", id));
                });
    }
}