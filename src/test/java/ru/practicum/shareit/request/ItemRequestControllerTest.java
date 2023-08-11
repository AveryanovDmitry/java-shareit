package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.RequestWasCreatedDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.ShareItApp.USER_ID_HEADER;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemRequestControllerTest {
    @MockBean
    private RequestService requestService;

    private final MockMvc mvc;

    private final ObjectMapper objectMapper;

    private ItemRequestDto itemRequestDto;

    private RequestWasCreatedDto requestWasCreatedDto;

    private RequestWithItemsDto requestWithItemsDto;

    @BeforeEach
    void setUp() {
        User user = new User(1L, "test", "test@yandex.ru");

        requestWasCreatedDto = new RequestWasCreatedDto();
        requestWasCreatedDto.setId(1L);
        requestWasCreatedDto.setDescription("Request description");
        requestWasCreatedDto.setRequester(user.getId());
        requestWasCreatedDto.setCreated(LocalDateTime.now());

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Request description");

        ItemResponseDto item = ItemResponseDto.builder()
                .name("item test")
                .description("item test description")
                .available(Boolean.TRUE)
                .requestId(user.getId())
                .build();
        ItemResponseDto item2 = ItemResponseDto.builder()
                .name("Item2 test")
                .description("item2 test description")
                .available(Boolean.TRUE)
                .requestId(user.getId())
                .build();

        requestWithItemsDto = RequestWithItemsDto.builder()
                .id(1)
                .description("RequestWithItemsDto description")
                .created(LocalDateTime.now())
                .items(List.of(item, item2))
                .build();
    }

    @Test
    void createRequestWrongIdUser() throws Exception {
        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .header(USER_ID_HEADER, 0)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRequestWhenIncorrectDescription() throws Exception {
        itemRequestDto.setDescription(null);
        mvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getByRequesterIdWithIncorrectUserId() throws Exception {
        mvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 0)
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isBadRequest());
        verify(requestService, times(0))
                .getRequestsByRequesterId(any(PageRequest.class), anyLong());
    }

    @Test
    void getByRequesterWithIncorrectParamFrom() throws Exception {
        mvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 1)
                        .param("from", "-1")
                        .param("size", "2"))
                .andExpect(status().isBadRequest());
        verify(requestService, times(0))
                .getRequestsByRequesterId(any(PageRequest.class), anyLong());
    }

    @Test
    void getByRequesterWithIncorrectParamSize() throws Exception {
        mvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 1)
                        .param("from", "1")
                        .param("size", "-2"))
                .andExpect(status().isBadRequest());
        verify(requestService, times(0))
                .getRequestsByRequesterId(any(PageRequest.class), anyLong());
    }

    @Test
    void getWithoutRequesterIdWithIncorrectUserId() throws Exception {
        mvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, 0)
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isBadRequest());
        verify(requestService, times(0))
                .getAllRequestsWithoutRequesterId(any(PageRequest.class), anyLong());
    }

    @Test
    void getWithoutRequesterWithIncorrectParamFrom() throws Exception {
        mvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, 1)
                        .param("from", "-1")
                        .param("size", "2"))
                .andExpect(status().isBadRequest());
        verify(requestService, times(0))
                .getAllRequestsWithoutRequesterId(any(PageRequest.class), anyLong());
    }

    @Test
    void getWithoutRequesterWithIncorrectParamSize() throws Exception {
        mvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, 1)
                        .param("from", "1")
                        .param("size", "-2"))
                .andExpect(status().isBadRequest());
        verify(requestService, times(0))
                .getAllRequestsWithoutRequesterId(any(PageRequest.class), anyLong());
    }

    @Test
    void getByIdWithIncorrectParam() throws Exception {
        mvc.perform(get("/requests/-1")
                        .header(USER_ID_HEADER, 1))
                .andExpect(status().isBadRequest());
        verify(requestService, times(0))
                .getRequestById(anyLong(), anyLong());
    }

    @Test
    void getByIdWithIncorrectHeader() throws Exception {
        mvc.perform(get("/requests/1")
                        .header(USER_ID_HEADER, -1))
                .andExpect(status().isBadRequest());
        verify(requestService, times(0))
                .getRequestById(anyLong(), anyLong());
    }

    @Test
    void createRequest() throws Exception {
        when(requestService.createRequest(any(), anyLong())).thenReturn(requestWasCreatedDto);

        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(requestWasCreatedDto.getDescription()), String.class));
    }

    @Test
    void getRequestsBySenderId() throws Exception {
        when(requestService.getRequestsByRequesterId(any(PageRequest.class), anyLong())).thenReturn(List.of(requestWithItemsDto));

        mvc.perform(get("/requests").header(USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description",
                        is(requestWithItemsDto.getDescription()), String.class))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getAllRequests() throws Exception {
        when(requestService.getAllRequestsWithoutRequesterId(any(PageRequest.class), anyLong()))
                .thenReturn(List.of(requestWithItemsDto));

        mvc.perform(get("/requests/all").header(USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description",
                        is(requestWithItemsDto.getDescription()), String.class))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getItemRequest() throws Exception {
        when(requestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(requestWithItemsDto);

        mvc.perform(get("/requests/{requestId}", 1).header(USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(requestWithItemsDto.getDescription()), String.class));
    }
}