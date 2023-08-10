package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeptions.BookingException;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.DESC;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    private final ItemService itemService;
    private final UserRepository userRepository;
    private final RequestItemRepository itemRequestRepository;
    @MockBean
    private BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private CreateUpdateItemDto itemCreate1;
    private CreateUpdateItemDto itemCreate2;
    private CreateUpdateItemDto itemUpdateDto;
    private User user1;
    private User user2;
    private ItemRequest itemRequest1;
    private Booking lastBooking;
    private Booking nextBooking;
    private ItemDto itemDto;

    private BookingDtoShort bookingDtoShortNext;

    private BookingDtoShort bookingDtoShort1Last;

    @BeforeEach
    public void setUp() {
        bookingDtoShortNext = new BookingDtoShort(1L, new ItemForBookingDto(1L, "item"),
                1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        bookingDtoShort1Last = new BookingDtoShort(1L, new ItemForBookingDto(1L, "item"),
                1, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        itemDto = ItemDto.builder()
                .id(1L)
                .name("item test")
                .description("item test description")
                .available(Boolean.TRUE)
                .build();
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
    void notExistingDescription() {
        userRepository.save(user1);
        itemCreate1.setDescription(null);
        assertThatThrownBy(() -> itemService.createItem(itemCreate1, user1.getId()))
                .isInstanceOf(Throwable.class);
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
    void getItemByIdWithNotExistingUser() {
        assertThatThrownBy(() -> itemService
                .getAllItemFromStorageByUserId(PageRequest.of(0, 2), 99L))
                .isInstanceOf(NotFoundException.class);
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
    void addComment() {
        CreateCommentDto commentDto = new CreateCommentDto("Nice item");
        userRepository.save(user1);
        userRepository.save(user2);
        ItemDto savedItem1 = itemService.createItem(itemCreate1, user1.getId());
        createLastAndNextBookings(savedItem1);
        lastBooking.setStart(LocalDateTime.now().minusDays(2));
        lastBooking.setEnd(LocalDateTime.now().minusDays(1));

        when(bookingRepository
                .findAllByItemIdAndBookerIdAndStatus(anyLong(), anyLong(), eq(StatusBooking.APPROVED),
                        eq(Sort.by(DESC, "start"))))
                .thenReturn(List.of(lastBooking));

        CommentDto savedComment1 = itemService.addComment(user2.getId(), savedItem1.getId(), commentDto);
        Comment comment1 = commentRepository.findById(savedComment1.getId()).get();

        assertThat(savedComment1.getId()).isEqualTo(1L);
        assertThat(savedComment1.getText()).isEqualTo(commentDto.getText());
        assertThat(savedComment1.getCreated()).isBefore(LocalDateTime.now());
        assertThat(savedComment1.getAuthorName()).isEqualTo(user2.getName());

        commentDto.setText("Nice item, awesome author2");
        var savedComment2 = itemService.addComment(user2.getId(), savedItem1.getId(), commentDto);
        var comment2 = commentRepository.findById(savedComment2.getId()).get();

        assertThat(comment1.equals(comment2)).isFalse();
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
        userRepository.save(user2);
        var savedItem1 = itemService.createItem(itemCreate1, user1.getId());
        createLastAndNextBookings(savedItem1);
        bookingRepository.save(lastBooking);
        assertThat(lastBooking.equals(nextBooking)).isFalse();
        assertThatThrownBy(() -> itemService.addComment(2L, user2.getId(), commentDto))
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

    void createLastAndNextBookings(ItemDto argItem) {
        Item bookingItem = new Item();
        bookingItem.setId(argItem.getId());
        bookingItem.setOwner(user1.getId());
        bookingItem.setName(argItem.getName());
        bookingItem.setDescription(argItem.getDescription());
        bookingItem.setAvailable(argItem.getAvailable());

        lastBooking = new Booking();
        lastBooking.setItem(bookingItem);
        lastBooking.setBooker(user2);
        lastBooking.setStatus(StatusBooking.APPROVED);

        nextBooking = new Booking();
        nextBooking.setStart(LocalDateTime.now().plusDays(1));
        nextBooking.setEnd(LocalDateTime.now().plusDays(2));
        nextBooking.setItem(bookingItem);
        nextBooking.setBooker(user2);
        nextBooking.setStatus(StatusBooking.APPROVED);
    }
}