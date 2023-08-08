package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    @Mock
    private RequestService requestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ItemRequestDto itemRequestDto;

    private RequestWasCreatedDto requestWasCreatedDto;

    private RequestWithItemsDto requestWithItemsDto;
    private static final String REQUESTER_ID = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(itemRequestController).build();
        User user = new User(1L, "test", "test@yandex.ru");

        requestWasCreatedDto = new RequestWasCreatedDto();
        requestWasCreatedDto.setId(1L);
        requestWasCreatedDto.setDescription("Request description");
        requestWasCreatedDto.setRequester(user);
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
    void createRequest() throws Exception {
        when(requestService.createRequest(any(), anyLong())).thenReturn(requestWasCreatedDto);

        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .header(REQUESTER_ID, 1)
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

        mvc.perform(get("/requests").header(REQUESTER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description",
                        is(requestWithItemsDto.getDescription()), String.class))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getAllRequests() throws Exception {
        when(requestService.getAllRequestsWithoutRequesterId(any(PageRequest.class), anyLong()))
                .thenReturn(List.of(requestWithItemsDto));

        mvc.perform(get("/requests/all").header(REQUESTER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description",
                        is(requestWithItemsDto.getDescription()), String.class))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getItemRequest() throws Exception {
        when(requestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(requestWithItemsDto);

        mvc.perform(get("/requests/{requestId}", 1).header(REQUESTER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(requestWithItemsDto.getDescription()), String.class));
    }
}