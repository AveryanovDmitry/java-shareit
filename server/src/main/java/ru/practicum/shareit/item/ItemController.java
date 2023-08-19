package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.List;

import static ru.practicum.shareit.ShareItApp.USER_ID_HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestBody CreateUpdateItemDto itemDto,
                              @RequestHeader(USER_ID_HEADER) Long ownerId) {
        log.info("Получен запрос создания новой вещи");
        return itemService.createItem(itemDto, ownerId);
    }

    @GetMapping("{id}")
    public ItemDto getItemByID(@PathVariable Long id, @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Получен запрос получения вещи по id");
        return itemService.getItemFromStorage(id, userId);
    }

    @DeleteMapping("{id}")
    public void deleteItemById(@PathVariable Long id) {
        log.info("Получен запрос удаления вещи по id");
        itemService.deleteItemFromStorage(id);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody CreateUpdateItemDto itemDto,
                              @PathVariable Long itemId,
                              @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Получен запрос обновления вещи по id");
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping()
    public List<ItemDto> getItemByIdOwner(@RequestHeader(USER_ID_HEADER) Long id,
                                          @RequestParam(defaultValue = "0") Integer from,
                                          @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос получения вещи по id владельца");
        return itemService.getAllItemFromStorageByUserId(PageRequest.of(from / size, size), id);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam(name = "text") String text,
                                           @RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос поиска вещи по тексту");
        return itemService.searchItemsByText(PageRequest.of(from / size, size), text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_ID_HEADER) long userId,
                                 @PathVariable long itemId,
                                 @RequestBody CreateCommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
