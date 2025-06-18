package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoCreate;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient;

    private final ItemDto testItem = new ItemDto(1L, "Test Item", "Test Description",
            1L, 1L, true, List.of());

    private final CommentDto testComment = new CommentDto(1L, "Test comment", "Author", LocalDateTime.now());

    @Test
    void getOwnerItemsReturnsListOfItems() throws Exception {
        when(itemClient.getOwnerItems(1L))
                .thenReturn(ResponseEntity.ok(List.of(testItem)));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testItem.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(testItem.getName())))
                .andExpect(jsonPath("$[0].description", is(testItem.getDescription())));

        verify(itemClient, times(1)).getOwnerItems(1L);
    }

    @Test
    void getItemByIdExistsReturnsItem() throws Exception {
        when(itemClient.getItemById(1L, 1L))
                .thenReturn(ResponseEntity.ok(testItem));

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testItem.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(testItem.getName())))
                .andExpect(jsonPath("$.description", is(testItem.getDescription())));

        verify(itemClient, times(1)).getItemById(1L, 1L);
    }

    @Test
    void createItemValidDataReturnsItem() throws Exception {
        when(itemClient.createItem(any(), eq(1L)))
                .thenReturn(ResponseEntity.ok(testItem));

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(testItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testItem.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(testItem.getName())))
                .andExpect(jsonPath("$.description", is(testItem.getDescription())));

        verify(itemClient, times(1)).createItem(any(), eq(1L));
    }

    @Test
    void createItemWithInvalidDataReturnsBadRequest() throws Exception {
        ItemDto invalidItem = new ItemDto(null, " ", " ", null, null, null, null);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(invalidItem))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(any(), anyLong());
    }

    @Test
    void updateItemValidDataReturnsUpdatedItem() throws Exception {
        ItemDto updatedItem = new ItemDto(1L, "Updated Item", "Updated Description",
                1L, 1L, false, List.of());
        when(itemClient.updateItem(eq(1L), any(), eq(1L)))
                .thenReturn(ResponseEntity.ok(updatedItem));

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(updatedItem))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedItem.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedItem.getName())))
                .andExpect(jsonPath("$.description", is(updatedItem.getDescription())));

        verify(itemClient, times(1)).updateItem(eq(1L), any(), eq(1L));
    }

    @Test
    void deleteItemExistsReturnsOk() throws Exception {
        when(itemClient.deleteItem(1L))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(delete("/items/1"))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).deleteItem(1L);
    }

    @Test
    void searchItemsByTextReturnsListOfItems() throws Exception {
        when(itemClient.searchItems("test"))
                .thenReturn(ResponseEntity.ok(List.of(testItem)));

        mvc.perform(get("/items/search?text=test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testItem.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(testItem.getName())));

        verify(itemClient, times(1)).searchItems("test");
    }

    @Test
    void createCommentValidDataReturnsComment() throws Exception {
        CommentDtoCreate commentDto = new CommentDtoCreate("Test comment");
        when(itemClient.createComment(eq(1L), any(), eq(1L)))
                .thenReturn(ResponseEntity.ok(testComment));

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testComment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(testComment.getText())))
                .andExpect(jsonPath("$.authorName", is(testComment.getAuthorName())));

        verify(itemClient, times(1)).createComment(eq(1L), any(), eq(1L));
    }

    @Test
    void createCommentWithEmptyTextReturnsBadRequest() throws Exception {
        CommentDtoCreate invalidComment = new CommentDtoCreate(" ");

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(invalidComment))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createComment(anyLong(), any(), anyLong());
    }
}
