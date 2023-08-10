package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemControllerTest {
    private final ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    private final MockMvc mvc;
    private ItemDto item1;
    private ItemDto itemDtoResponse;
    private CreateUpdateItemDto itemDtoUpdate;
    private final String userIdHeader = "X-Sharer-User-Id";

    @BeforeEach
    public void setUp() {
        item1 = ItemDto.builder()
                .name("item test")
                .description("item test description")
                .available(Boolean.TRUE)
                .build();
        itemDtoResponse = ItemDto.builder()
                .id(1L)
                .name(item1.getName())
                .description(item1.getDescription())
                .available(Boolean.TRUE)
                .build();
        itemDtoUpdate = CreateUpdateItemDto.builder()
                .name("update item test")
                .description("update test description")
                .build();
    }

    @Test
    void createItem() throws Exception {
        when(itemService.createItem(any(CreateUpdateItemDto.class), anyLong())).thenReturn(itemDtoResponse);
        mvc.perform(post("/items")
                        .header(userIdHeader, 1)
                        .content(objectMapper.writeValueAsString(item1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(itemDtoResponse))
                );
    }

    @SneakyThrows
    @Test
    void createItemWithIncorrectUserId() {
        mvc.perform(post("/items")
                        .header(userIdHeader, 0)
                        .content(objectMapper.writeValueAsString(item1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(itemService, times(0)).createItem(any(CreateUpdateItemDto.class), anyLong());
    }

    @SneakyThrows
    @Test
    void createItemWithIncorrectName() {
        item1.setName("  test name");
        mvc.perform(post("/items")
                        .header(userIdHeader, 1)
                        .content(objectMapper.writeValueAsString(item1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(itemService, times(0)).createItem(any(CreateUpdateItemDto.class), anyLong());
    }

    @SneakyThrows
    @Test
    void createItemWithIncorrectDescription() {
        item1.setDescription("  test description");
        mvc.perform(post("/items")
                        .header(userIdHeader, 1)
                        .content(objectMapper.writeValueAsString(item1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, times(0)).createItem(any(CreateUpdateItemDto.class), anyLong());
    }

    @SneakyThrows
    @Test
    void createItemWithIncorrectAvailable() {
        item1.setAvailable(null);
        mvc.perform(post("/items")
                        .header(userIdHeader, 1)
                        .content(objectMapper.writeValueAsString(item1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(itemService, times(0)).createItem(any(CreateUpdateItemDto.class), anyLong());
    }

    @SneakyThrows
    @Test
    void createItemWithIncorrectIdRequest() {
        item1.setRequestId(0L);
        mvc.perform(post("/items")
                        .header(userIdHeader, 1)
                        .content(objectMapper.writeValueAsString(item1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(itemService, times(0)).createItem(any(CreateUpdateItemDto.class), anyLong());
    }

    @SneakyThrows
    @Test
    void updateItem() {
        itemDtoResponse.setName(itemDtoUpdate.getName());
        itemDtoResponse.setDescription(itemDtoUpdate.getDescription());
        when(itemService.updateItem(any(CreateUpdateItemDto.class), anyLong(), anyLong())).thenReturn(itemDtoResponse);
        mvc.perform(
                        patch("/items/1")
                                .header(userIdHeader, 1)
                                .content(objectMapper.writeValueAsString(item1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(itemDtoResponse))
                );
    }

    @SneakyThrows
    @Test
    void updateItemWithIncorrectUserId() {
        mvc.perform(patch("/items/1")
                        .header(userIdHeader, 0)
                        .content(objectMapper.writeValueAsString(item1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(itemService, times(0))
                .updateItem(any(CreateUpdateItemDto.class), anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void updateItemWithIncorrectItemId() {
        mvc.perform(patch("/items/0")
                        .header(userIdHeader, 1)
                        .content(objectMapper.writeValueAsString(item1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(itemService, times(0))
                .updateItem(any(CreateUpdateItemDto.class), anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void updateItemWithIncorrectName() {
        itemDtoUpdate.setName("    updated name");
        mvc.perform(patch("/items/0")
                        .header(userIdHeader, 1)
                        .content(objectMapper.writeValueAsString(item1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(itemService, times(0))
                .updateItem(any(CreateUpdateItemDto.class), anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void updateItemWithIncorrectDescription() {
        itemDtoUpdate.setDescription("   updated description");
        mvc.perform(patch("/items/0")
                        .header(userIdHeader, 1)
                        .content(objectMapper.writeValueAsString(item1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(itemService, times(0))
                .updateItem(any(CreateUpdateItemDto.class), anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getItemById() {
        when(itemService.getItemFromStorage(anyLong(), anyLong())).thenReturn(itemDtoResponse);
        mvc.perform(get("/items/1")
                        .header(userIdHeader, 1))
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(itemDtoResponse))
                );
    }

    @SneakyThrows
    @Test
    void getItemByIdWithIncorrectUserId() {
        mvc.perform(get("/items/1")
                        .header(userIdHeader, 0))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(itemService, times(0)).getItemFromStorage(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getItemByIncorrectId() {
        mvc.perform(get("/items/0")
                        .header(userIdHeader, 1))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(itemService, times(0)).getItemFromStorage(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getPersonalItems() {
        List<ItemDto> itemListDto = List.of(itemDtoResponse);
        when(itemService.getAllItemFromStorageByUserId(any(PageRequest.class), anyLong()))
                .thenReturn(itemListDto);
        mvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", 1))
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(itemListDto))
                );
    }

    @SneakyThrows
    @Test
    void getPersonalItemsWithIncorrectUserId() {
        mvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "1")
                        .header(userIdHeader, 0))
                .andExpect(status().isBadRequest());
        verify(itemService, times(0))
                .getAllItemFromStorageByUserId(any(PageRequest.class), anyLong());
    }

    @SneakyThrows
    @Test
    void getPersonalItemsWithIncorrectParamFrom() {
        mvc.perform(get("/items")
                        .param("from", "-1")
                        .param("size", "1")
                        .header(userIdHeader, 1))
                .andExpectAll(status().isBadRequest());
        verify(itemService, times(0))
                .getAllItemFromStorageByUserId(any(PageRequest.class), anyLong());
    }

    @SneakyThrows
    @Test
    void getPersonalItemsWithIncorrectParamSize() {
        mvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "99999")
                        .header(userIdHeader, 1))
                .andExpect(status().isBadRequest());
        verify(itemService, times(0))
                .getAllItemFromStorageByUserId(any(PageRequest.class), anyLong());
    }

    @SneakyThrows
    @Test
    void getFoundItems() {
        List<ItemDto> itemListDto = List.of(itemDtoResponse);
        when(itemService.searchItemsByText(any(PageRequest.class), anyString())).thenReturn(itemListDto);
        mvc.perform(get("/items/search")
                        .param("from", "0")
                        .param("size", "1")
                        .param("text", "description"))
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(itemListDto))
                );
    }

    @SneakyThrows
    @Test
    void getFoundItemsWitchIncorrectParamFrom() {
        mvc.perform(get("/items/search")
                        .param("from", "-1")
                        .param("size", "1")
                        .param("text", "description"))
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(itemService, times(0)).searchItemsByText(any(PageRequest.class), anyString());
    }

    @SneakyThrows
    @Test
    void getFoundItemsWitchIncorrectParamSize() {
        mvc.perform(get("/items/search")
                        .param("from", "0")
                        .param("size", "0")
                        .param("text", "description"))
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(itemService, times(0)).searchItemsByText(any(PageRequest.class), anyString());
    }

    @SneakyThrows
    @Test
    void addComment() {
        CreateCommentDto comment = new CreateCommentDto("comment");
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .authorName(item1.getName())
                .text(comment.getText())
                .created(LocalDateTime.now())
                .build();

        when(itemService.addComment(anyLong(), anyLong(), any(CreateCommentDto.class))).thenReturn(commentDto);
        mvc.perform(post("/items/1/comment")
                        .header(userIdHeader, 1)
                        .content(objectMapper.writeValueAsString(comment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(commentDto))
                );
    }

    @SneakyThrows
    @Test
    void addCommentWithEmptyText() {
        CreateCommentDto comment = new CreateCommentDto("     ");
        mvc.perform(post("/items/1/comment")
                        .header(userIdHeader, 1)
                        .content(objectMapper.writeValueAsString(comment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(itemService, times(0)).addComment(anyLong(), anyLong(), any(CreateCommentDto.class));
    }

    @SneakyThrows
    @Test
    void addCommentWithIncorrectItemId() {
        var comment = CommentDto.builder()
                .text("     ")
                .build();
        mvc.perform(post("/items/0/comment")
                        .header(userIdHeader, 1)
                        .content(objectMapper.writeValueAsString(comment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(itemService, times(0)).addComment(anyLong(), anyLong(), any(CreateCommentDto.class));
    }

    @SneakyThrows
    @Test
    void addCommentWithIncorrectUserId() {
        CommentDto comment = CommentDto.builder().text("     ").build();
        mvc.perform(post("/items/1/comment")
                        .header(userIdHeader, 0)
                        .content(objectMapper.writeValueAsString(comment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(itemService, times(0))
                .addComment(anyLong(), anyLong(), any(CreateCommentDto.class));
    }
}