package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    private final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private final ItemDto testItem = ItemDto.builder()
            .id(1L)
            .name("Test Item")
            .description("Test Description")
            .available(true)
            .build();

    private final ItemOwnerDto testItemOwner = ItemOwnerDto.builder()
            .id(1L)
            .name("Test Item")
            .description("Test Description")
            .available(true)
            .build();

    private final CommentDto testComment = CommentDto.builder()
            .id(1L)
            .text("Test Comment")
            .authorName("Test Author")
            .created(LocalDateTime.now())
            .build();

    @Test
    void getOwnerItemsShouldReturnListOfItems() throws Exception {
        when(itemService.getOwnerItems(anyLong())).thenReturn(List.of(testItemOwner));

        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(testItemOwner.getId()))
                .andExpect(jsonPath("$[0].name").value(testItemOwner.getName()));

        verify(itemService).getOwnerItems(anyLong());
    }

    @Test
    void getItemByIdShouldReturnItem() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(testItemOwner);

        mockMvc.perform(get("/items/1")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testItemOwner.getId()))
                .andExpect(jsonPath("$.name").value(testItemOwner.getName()));

        verify(itemService).getItemById(anyLong(), anyLong());
    }

    @Test
    void createItemWithValidRequestShouldReturnCreatedItem() throws Exception {
        when(itemService.createItem(any(ItemDto.class), anyLong())).thenReturn(testItem);

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testItem.getId()))
                .andExpect(jsonPath("$.name").value(testItem.getName()));

        verify(itemService).createItem(any(ItemDto.class), anyLong());
    }

    @Test
    void updateItemShouldReturnUpdatedItem() throws Exception {
        when(itemService.updateItem(any(ItemDto.class), anyLong(), anyLong())).thenReturn(testItem);

        mockMvc.perform(patch("/items/1")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testItem.getId()))
                .andExpect(jsonPath("$.name").value(testItem.getName()));

        verify(itemService).updateItem(any(ItemDto.class), anyLong(), anyLong());
    }

    @Test
    void deleteItemShouldReturnOk() throws Exception {
        doNothing().when(itemService).deleteItem(anyLong());

        mockMvc.perform(delete("/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService).deleteItem(anyLong());
    }

    @Test
    void searchItemsByTextShouldReturnListOfItems() throws Exception {
        when(itemService.searchItemsByText(anyString())).thenReturn(List.of(testItem));

        mockMvc.perform(get("/items/search")
                        .param("text", "test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(testItem.getId()));

        verify(itemService).searchItemsByText(anyString());
    }

    @Test
    void createCommentShouldReturnCreatedComment() throws Exception {
        when(itemService.createComment(anyLong(), any(CommentDto.class), anyLong())).thenReturn(testComment);

        mockMvc.perform(post("/items/1/comment")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testComment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testComment.getId()))
                .andExpect(jsonPath("$.text").value(testComment.getText()));

        verify(itemService).createComment(anyLong(), any(CommentDto.class), anyLong());
    }

    @Test
    void getItemByIdWhenNotFoundShouldReturn404() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Item not found"));

        mockMvc.perform(get("/items/999")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemService).getItemById(anyLong(), anyLong());
    }

    @Test
    void createItemWithInvalidRequestShouldReturnBadRequest() throws Exception {
        ItemDto invalidItem = ItemDto.builder().build();

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidItem)))
                .andExpect(status().isBadRequest());
    }
}