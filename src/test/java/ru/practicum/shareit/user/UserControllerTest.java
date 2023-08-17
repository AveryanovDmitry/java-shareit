package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class UserControllerTest {
    private final ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    private final MockMvc mvc;
    private static UserDto userDtoResponse;
    private static UserDto userDto;
    private static UserDto userDtoUpdate;

    @BeforeEach
    public void setUp() {
        userDtoResponse = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@test.ru")
                .build();
        userDto = UserDto.builder()
                .name("test")
                .email("test@test.ru")
                .build();
        userDtoUpdate = UserDto.builder()
                .name("test")
                .email("test@test.ru")
                .build();
    }

    @Test
    void createUser() throws Exception {
        when(userService.createUser(any(UserDto.class))).thenReturn(userDtoResponse);
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(userDtoResponse))
                );
    }

    @Test
    void createUserWithIncorrectName() throws Exception {
        UserDto userDtoWithIncorrectName = UserDto.builder()
                .name("  incorrect name")
                .build();
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoWithIncorrectName))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(userService, times(0)).createUser(any(UserDto.class));
    }

    @Test
    void createUserWithIncorrectEmail() throws Exception {
        UserDto userDtoWithIncorrectEmail = UserDto.builder()
                .name("test name")
                .email("incorrect-email@.ru")
                .build();
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoWithIncorrectEmail))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(userService, times(0)).createUser(any(UserDto.class));
    }

    @Test
    void getUserById() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(userDtoResponse);
        mvc.perform(get("/users/1"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(userDtoResponse))
                );
    }

    @Test
    void getUserByIncorrectId() throws Exception {
        mvc.perform(get("/users/-1"))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
    }

    @Test
    void getUsers() throws Exception {
        List<UserDto> userList = List.of(userDtoResponse);
        when(userService.getAllUsers()).thenReturn(userList);
        mvc.perform(get("/users"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(userList))
                );
    }

    @Test
    void updateUser() throws Exception {
        when(userService.updateUserById(any(UserDto.class), anyLong())).thenReturn(userDtoResponse);
        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userDtoUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(userDtoResponse))
                );
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/1"))
                .andDo(print())
                .andExpectAll(
                        status().isOk()
                );
        verify(userService, times(1)).deleteUserById(1L);
    }

    @Test
    void updateUserWithIncorrectEmail() throws Exception {
        UserDto userDtoWithIncorrectEmail = UserDto.builder()
                .name("test name")
                .email("incorrect-email@.ru")
                .build();
        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userDtoWithIncorrectEmail))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(userService, times(0)).updateUserById(any(UserDto.class), anyLong());
    }
}