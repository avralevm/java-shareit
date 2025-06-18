package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingRequest;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private final BookingDto testBooking = BookingDto.builder()
            .id(1L)
            .start(LocalDateTime.now().plusHours(1))
            .end(LocalDateTime.now().plusHours(2))
            .status(BookingStatus.WAITING)
            .item(ItemDto.builder().id(1L).name("Test Item").build())
            .booker(UserDto.builder().id(1L).name("Test User").build())
            .build();

    private final BookingRequest testBookingRequest = BookingRequest.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusHours(1))
            .end(LocalDateTime.now().plusHours(2))
            .build();

    @Test
    void createBookingShouldReturnCreatedBooking() throws Exception {
        when(bookingService.createBooking(any(BookingRequest.class), anyLong())).thenReturn(testBooking);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testBooking.getId()))
                .andExpect(jsonPath("$.status").value(testBooking.getStatus().name()));

        verify(bookingService).createBooking(any(BookingRequest.class), anyLong());
    }

    @Test
    void getBookingShouldReturnBooking() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(testBooking);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testBooking.getId()));

        verify(bookingService).getBookingById(anyLong(), anyLong());
    }

    @Test
    void approveBookingShouldReturnUpdatedBooking() throws Exception {
        BookingDto approvedBooking = testBooking.toBuilder()
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(approvedBooking);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService).approveBooking(anyLong(), anyLong(), eq(true));
    }

    @Test
    void getUserBookingsShouldReturnListOfBookings() throws Exception {
        when(bookingService.getUserBookings(anyLong(), any(BookingState.class)))
                .thenReturn(List.of(testBooking));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(testBooking.getId()));

        verify(bookingService).getUserBookings(anyLong(), any(BookingState.class));
    }

    @Test
    void getOwnerBookingsShouldReturnListOfBookings() throws Exception {
        when(bookingService.getOwnerBookings(anyLong(), any(BookingState.class)))
                .thenReturn(List.of(testBooking));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(testBooking.getId()));

        verify(bookingService).getOwnerBookings(anyLong(), any(BookingState.class));
    }

    @Test
    void createBookingWithInvalidDatesShouldReturnBadRequest() throws Exception {
        BookingRequest invalidRequest = BookingRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        when(bookingService.createBooking(any(BookingRequest.class), anyLong()))
                .thenThrow(new ValidationException("Invalid dates"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingWhenNotAuthorizedShouldReturnForbidden() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenThrow(new ValidationException("Not authorized"));

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveBookingWhenNotOwnerShouldReturnForbidden() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new ValidationException("Not owner"));

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBookingWithMissingHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookingRequest)))
                .andExpect(status().isBadRequest());
    }
}