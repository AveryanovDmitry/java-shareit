package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.exeptions.BookingException;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemServiceTest {
    private final ItemService itemService;
    private final UserRepository userRepository;
    private final RequestItemRepository itemRequestRepository;
    private CreateUpdateItemDto itemCreate1;
    private CreateUpdateItemDto itemCreate2;
    private CreateUpdateItemDto itemUpdateDto;
    private User user1;
    private User user2;
    private ItemRequest itemRequest1;

    @BeforeEach
    public void setUp() {
        itemCreate1 = CreateUpdateItemDto.builder()
                .name("item test")
                .description("item test description")
                .available(Boolean.TRUE)
                .build();
        itemCreate2 = CreateUpdateItemDto.builder()
                .name("item2 test")
                .description("item2 test description")
                .available(Boolean.TRUE)
                .build();
        itemUpdateDto = CreateUpdateItemDto.builder()
                .name("updated name")
                .description("updated description")
                .available(Boolean.FALSE)
                .build();
        user1 = new User();
        user1.setName("test name");
        user1.setEmail("test@test.ru");
        user2 = new User();
        user2.setName("test name2");
        user2.setEmail("test2@test.ru");
        itemRequest1 = new ItemRequest();
        itemRequest1.setDescription("item request1 description");
        itemRequest1.setRequester(user2);
        itemRequest1.setCreated(LocalDateTime.now());
    }

    @Test
    void createAndGetItemById() {
        userRepository.save(user1);
        ItemDto savedItem = itemService.createItem(itemCreate1, user1.getId());
        var findItem = itemService.getItemFromStorage(savedItem.getId(), user1.getId());
        assertThat(savedItem).usingRecursiveComparison().ignoringFields("comments").isEqualTo(findItem);
    }

    @Test
    void notExistingUserCreateItem() {
        assertThatThrownBy(() -> itemService.createItem(itemCreate1, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void createItemWithItemRequest() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRequestRepository.save(itemRequest1);
        itemCreate1.setRequestId(itemRequest1.getId());

        ItemDto savedItem = itemService.createItem(itemCreate1, user1.getId());
        ItemDto findItem = itemService.getItemFromStorage(savedItem.getId(), user2.getId());
        ItemRequest saveRequest = itemRequestRepository.findById(itemRequest1.getId()).get();
        assertThat(savedItem).usingRecursiveComparison().ignoringFields("comments").isEqualTo(findItem);
        assertThat(saveRequest.equals(itemRequest1)).isFalse();
        assertThat(user1.equals(user2)).isFalse();
    }

    @Test
    void createItemWithNotExistingItemRequest() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRequestRepository.save(itemRequest1);
        itemCreate1.setRequestId(2L);
        assertThatThrownBy(() -> itemService.createItem(itemCreate1, user1.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateItem() {
        userRepository.save(user1);
        ItemDto savedItem = itemService.createItem(itemCreate1, user1.getId());
        ItemDto updatedItem = itemService.updateItem(itemUpdateDto, savedItem.getId(), user1.getId());
        assertThat(updatedItem.getId()).isEqualTo(savedItem.getId());
        assertThat(updatedItem.getName()).isEqualTo(itemUpdateDto.getName());
        assertThat(updatedItem.getDescription()).isEqualTo(itemUpdateDto.getDescription());
        assertThat(updatedItem.getAvailable()).isEqualTo(itemUpdateDto.getAvailable());
    }

    @Test
    void updateItemWithNotExistingItemId() {
        userRepository.save(user1);
        itemService.createItem(itemCreate1, user1.getId());
        assertThatThrownBy(() -> itemService.updateItem(itemUpdateDto, 2L, user1.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateItemWithOtherUser() {
        userRepository.save(user1);
        ItemDto savedItem = itemService.createItem(itemCreate1, user1.getId());
        assertThatThrownBy(() -> itemService.updateItem(itemUpdateDto, savedItem.getId(), 2L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getItemByNotExistingId() {
        userRepository.save(user1);
        itemService.createItem(itemCreate1, user1.getId());
        assertThatThrownBy(() -> itemService.getItemFromStorage(2L, user1.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void searchItemsByText() {
        userRepository.save(user1);
        ItemDto savedItem1 = itemService.createItem(itemCreate1, user1.getId());
        List<ItemDto> findItems = itemService.searchItemsByText(PageRequest.of(0, 2), "test");
        assertThat(findItems.size()).isEqualTo(1);
        assertThat(findItems.get(0)).usingRecursiveComparison()
                .ignoringFields("comments").isEqualTo(savedItem1);
    }

    @Test
    void getFoundItemsWhenSearchTextIsBlank() {
        userRepository.save(user1);
        itemService.createItem(itemCreate1, user1.getId());
        itemService.createItem(itemCreate2, user1.getId());
        List<ItemDto> findItems = itemService.searchItemsByText(PageRequest.of(0, 2), " ");
        assertThat(findItems).isEmpty();
    }

    @Test
    void addCommentWithNotExistingBooks() {
        CreateCommentDto commentDto = new CreateCommentDto("Nice item");
        userRepository.save(user1);
        userRepository.save(user2);
        ItemDto savedItem1 = itemService.createItem(itemCreate1, user1.getId());
        assertThatThrownBy(() -> itemService.addComment(user2.getId(), savedItem1.getId(), commentDto))
                .isInstanceOf(BookingException.class);
    }

    @Test
    void addCommentForNotExistingItem() {
        CreateCommentDto commentDto = new CreateCommentDto("Nice item");
        userRepository.save(user1);
        assertThatThrownBy(() -> itemService.addComment(2L, user1.getId(), commentDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void addCommentFromNotExistingUser() {
        CreateCommentDto commentDto = new CreateCommentDto("Nice item");
        userRepository.save(user1);
        ItemDto savedItem = itemService.createItem(itemCreate1, user1.getId());
        assertThatThrownBy(() -> itemService.addComment(savedItem.getId(), 50L, commentDto))
                .isInstanceOf(NotFoundException.class);
    }
}