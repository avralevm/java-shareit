package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserClient userClient;

    private final UserDto testUser = new UserDto(1L, "John Doe", "john@example.com");

    @Test
    void findAllUsersReturnsListOfUsers() throws Exception {
        List<UserDto> users = List.of(testUser);
        when(userClient.findAll())
                .thenReturn(ResponseEntity.ok(users));

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testUser.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(testUser.getName())))
                .andExpect(jsonPath("$[0].email", is(testUser.getEmail())));

        verify(userClient, times(1)).findAll();
    }

    @Test
    void getUserByIdExistsReturnsUser() throws Exception {
        when(userClient.getUserById(1L))
                .thenReturn(ResponseEntity.ok(testUser));

        mvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(testUser.getName())))
                .andExpect(jsonPath("$.email", is(testUser.getEmail())));

        verify(userClient, times(1)).getUserById(1L);
    }

    @Test
    void getUserByIdNotExistsReturnsNotFound() throws Exception {
        when(userClient.getUserById(999L))
                .thenReturn(ResponseEntity.notFound().build());

        mvc.perform(get("/users/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userClient, times(1)).getUserById(999L);
    }

    @Test
    void createUserValidDataReturnsUser() throws Exception {
        when(userClient.createUser(any()))
                .thenReturn(ResponseEntity.ok(testUser));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(testUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(testUser.getName())))
                .andExpect(jsonPath("$.email", is(testUser.getEmail())));

        verify(userClient, times(1)).createUser(any());
    }

    @Test
    void createUserWithInvalidDataReturnsBadRequest() throws Exception {
        UserDto invalidUser = new UserDto(null, "", "invalid-email");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(invalidUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any());
    }

    @Test
    void updateUserValidDataReturnsUpdatedUser() throws Exception {
        UserDto updatedUser = new UserDto(1L, "Updated Name", "updated@example.com");
        when(userClient.updateUser(eq(1L), any()))
                .thenReturn(ResponseEntity.ok(updatedUser));

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updatedUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedUser.getName())))
                .andExpect(jsonPath("$.email", is(updatedUser.getEmail())));

        verify(userClient, times(1)).updateUser(eq(1L), any());
    }

    @Test
    void updateUserWithInvalidEmailReturnsBadRequest() throws Exception {
        UserDto invalidUser = new UserDto(1L, "Name", "invalid-email");

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(invalidUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).updateUser(anyLong(), any());
    }

    @Test
    void deleteUserExistsReturnsOk() throws Exception {
        when(userClient.deleteUser(1L))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userClient, times(1)).deleteUser(1L);
    }

    @Test
    void deleteUserNotExistsReturnsNotFound() throws Exception {
        when(userClient.deleteUser(999L))
                .thenReturn(ResponseEntity.notFound().build());

        mvc.perform(delete("/users/999"))
                .andExpect(status().isNotFound());

        verify(userClient, times(1)).deleteUser(999L);
    }
}