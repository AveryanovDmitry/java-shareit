package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.RequestWasCreatedDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.mapper.RequestItemMapper;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemRequestServiceTest {
    private final RequestService requestService;
    private final UserRepository userRepository;

    private final RequestItemMapper requestItemMapper;
    private User user1;
    private User user2;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    public void setUp() {
        user1 = new User();
        user1.setName("test name");
        user1.setEmail("test@test.ru");

        user2 = new User();
        user2.setName("test name2");
        user2.setEmail("test2@test.ru");

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("test request description");
    }

    @Test
    void createItemRequest() {
        userRepository.save(user1);

        RequestWasCreatedDto savedRequest = requestService.createRequest(itemRequestDto, user1.getId());
        RequestWithItemsDto findRequest = requestService.getRequestById(user1.getId(), savedRequest.getId());

        assertThat(savedRequest.getDescription()).isEqualTo(findRequest.getDescription());
        assertThat(savedRequest.getCreated().getYear()).isEqualTo(findRequest.getCreated().getYear());
        assertThat(savedRequest.getCreated().getMonth()).isEqualTo(findRequest.getCreated().getMonth());
        assertThat(savedRequest.getCreated().getDayOfMonth()).isEqualTo(findRequest.getCreated().getDayOfMonth());
    }

    @Test
    void requesterNotFound() {
        userRepository.save(user1);
        assertThatThrownBy(() -> requestService.createRequest(itemRequestDto, 99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getRequestsByRequesterId() {
        userRepository.save(user1);
        userRepository.save(user2);

        RequestWasCreatedDto savedRequest = requestService.createRequest(itemRequestDto, user2.getId());

        System.out.println(savedRequest.getId());

        List<RequestWithItemsDto> privateRequests = requestService
                .getRequestsByRequesterId(PageRequest.of(0, 10), user2.getId());

        RequestWithItemsDto findRequest = requestService.getRequestById(user2.getId(), savedRequest.getId());

        assertThat(privateRequests.get(0)).usingRecursiveComparison().isEqualTo(findRequest);
        assertThat(privateRequests.get(0).getDescription()).isEqualTo(findRequest.getDescription());
        assertThat(privateRequests.get(0).getCreated().getYear()).isEqualTo(findRequest.getCreated().getYear());
        assertThat(privateRequests.get(0).getCreated().getMonthValue())
                .isEqualTo(findRequest.getCreated().getMonthValue());
        assertThat(privateRequests.get(0).getCreated().getDayOfMonth())
                .isEqualTo(findRequest.getCreated().getDayOfMonth());
    }

    @Test
    void getPrivateRequestWhenRequesterNotExistingRequests() {
        assertThatThrownBy(() -> requestService.getRequestsByRequesterId(PageRequest.of(0, 10), 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getAllRequestsWithoutRequesterId() {
        userRepository.save(user1);
        userRepository.save(user2);

        RequestWasCreatedDto savedRequest = requestService.createRequest(itemRequestDto, user1.getId());

        RequestWithItemsDto findRequest = requestService.getRequestById(user1.getId(), savedRequest.getId());

        List<RequestWithItemsDto> otherRequest
                = requestService.getAllRequestsWithoutRequesterId(PageRequest.of(0, 10), user2.getId());

        assertThat(otherRequest.get(0)).usingRecursiveComparison().isEqualTo(findRequest);
        assertThat(otherRequest.get(0).getDescription()).isEqualTo(findRequest.getDescription());
        assertThat(otherRequest.get(0).getCreated().getYear()).isEqualTo(findRequest.getCreated().getYear());
        assertThat(otherRequest.get(0).getCreated().getMonthValue())
                .isEqualTo(findRequest.getCreated().getMonthValue());
        assertThat(otherRequest.get(0).getCreated().getDayOfMonth())
                .isEqualTo(findRequest.getCreated().getDayOfMonth());
    }

    @Test
    void getAllRequestsWithoutRequesterIdNotFound() {
        userRepository.save(user1);
        requestService.createRequest(itemRequestDto, user1.getId());
        assertThatThrownBy(() -> requestService
                .getAllRequestsWithoutRequesterId(PageRequest.of(0, 10), 50L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getItemRequestWhenUserNotFound() {
        userRepository.save(user1);
        RequestWasCreatedDto savedRequest = requestService.createRequest(itemRequestDto, user1.getId());
        assertThatThrownBy(() -> requestService.getRequestById(2L, savedRequest.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getItemRequestWhenRequestNotFound() {
        userRepository.save(user1);
        RequestWasCreatedDto savedRequest = requestService.createRequest(itemRequestDto, user1.getId());
        assertThatThrownBy(() -> requestService.getRequestById(savedRequest.getId(), 2L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void mapperTest() {
        Item item1 = Item.builder()
                .name("Item1 test")
                .description("item1 test description")
                .available(Boolean.TRUE)
                .owner(user2.getId())
                .build();
        ItemResponseDto itemResponseDto = requestItemMapper.mapToItemDataForRequestDto(item1);

        assertThat(item1.getName()).isEqualTo(itemResponseDto.getName());
        assertThat(item1.getDescription()).isEqualTo(itemResponseDto.getDescription());
        assertThat(item1.getAvailable()).isEqualTo(itemResponseDto.getAvailable());
    }
}