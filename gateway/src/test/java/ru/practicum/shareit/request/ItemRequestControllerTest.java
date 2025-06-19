package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemRequestCreateDto testRequest = new ItemRequestCreateDto("Need a drill for home project");

    @Test
    void createItemRequestWithValidDataReturnsOk() throws Exception {
        when(itemRequestClient.createItemRequest(anyLong(), any(ItemRequestCreateDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(testRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemRequestClient, times(1)).createItemRequest(anyLong(), any(ItemRequestCreateDto.class));
    }

    @Test
    void createItemRequestWithInvalidDataReturnsBadRequest() throws Exception {
        ItemRequestCreateDto invalidRequest = new ItemRequestCreateDto(null);

        mvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).createItemRequest(anyLong(), any());
    }

    @Test
    void createItemRequestWithoutUserIdReturnsBadRequest() throws Exception {
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(testRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).createItemRequest(anyLong(), any());
    }

    @Test
    void getUserItemRequestsReturnsOk() throws Exception {
        when(itemRequestClient.getUserItemRequests(1L))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemRequestClient, times(1)).getUserItemRequests(1L);
    }

    @Test
    void getOtherUsersItemRequestsReturnsOk() throws Exception {
        when(itemRequestClient.getOtherUsersItemRequests(1L))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemRequestClient, times(1)).getOtherUsersItemRequests(1L);
    }

    @Test
    void getItemRequestByIdReturnsOk() throws Exception {
        when(itemRequestClient.getItemRequestById(1L, 1L))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/requests/1")
                        .header(USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemRequestClient, times(1)).getItemRequestById(1L, 1L);
    }

    @Test
    void getItemRequestByIdNotExistsReturnsNotFound() throws Exception {
        when(itemRequestClient.getItemRequestById(1L, 999L))
                .thenReturn(ResponseEntity.notFound().build());

        mvc.perform(get("/requests/999")
                        .header(USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemRequestClient, times(1)).getItemRequestById(1L, 999L);
    }
}