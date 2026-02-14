package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingClient bookingClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookItemRequestDto validRequest = new BookItemRequestDto(
            1L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
    );

    @Test
    void createBookingValidDataReturnsOk() throws Exception {
        when(bookingClient.createBooking(anyLong(), any()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).createBooking(eq(1L), any());
    }

    @Test
    void createBookingInvalidDataReturnsBadRequest() throws Exception {
        BookItemRequestDto invalidRequest = new BookItemRequestDto(
                0L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(2)
        );

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).createBooking(anyLong(), any());
    }

    @Test
    void getBookingExistsReturnsOk() throws Exception {
        when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/bookings/1")
                        .header(USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBooking(eq(1L), eq(1L));
    }

    @Test
    void approveBookingValidReturnsOk() throws Exception {
        when(bookingClient.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(patch("/bookings/1?approved=true")
                        .header(USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).approveBooking(eq(1L), eq(1L), eq(true));
    }

    @Test
    void getUserBookingsValidStateReturnsOk() throws Exception {
        when(bookingClient.getUserBookings(anyLong(), any()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/bookings?state=ALL")
                        .header(USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getUserBookings(eq(1L), eq(BookingState.ALL));
    }


    @Test
    void getOwnerBookingsValidStateReturnsOk() throws Exception {
        when(bookingClient.getOwnerBookings(anyLong(), any()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/bookings/owner?state=FUTURE")
                        .header(USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getOwnerBookings(eq(1L), eq(BookingState.FUTURE));
    }
}