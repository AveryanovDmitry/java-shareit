package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private static final String OWNER_ID = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Validated(CreateUpdateItemDto.Create.class) @RequestBody CreateUpdateItemDto itemDto,
                              @RequestHeader(OWNER_ID) Long ownerId) {
        log.info("Получен запрос создания новой вещи");
        return itemService.createItem(itemDto, ownerId);
    }

    @GetMapping("{id}")
    public ItemDto getItemByID(@PathVariable Long id, @RequestHeader(OWNER_ID) long userId) {
        log.info("Получен запрос получения вещи по id");
        return itemService.getItemFromStorage(id, userId);
    }

    @DeleteMapping("{id}")
    public void deleteItemById(@PathVariable Long id) {
        log.info("Получен запрос удаления вещи по id");
        itemService.deleteItemFromStorage(id);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Validated(CreateUpdateItemDto.Update.class) @RequestBody CreateUpdateItemDto itemDto, @PathVariable Long itemId,
                              @RequestHeader(OWNER_ID) Long userId) {
        log.info("Получен запрос обновления вещи по id");
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping()
    public List<ItemDto> getItemByIdOwner(@RequestHeader(OWNER_ID) Long id) {
        log.info("Получен запрос получения вещи по id владельца");
        return itemService.getAllItemFromStorageByUserId(id);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam(name = "text") String text) {
        log.info("Получен запрос поиска вещи по тексту");
        return itemService.searchItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(OWNER_ID) long userId, @PathVariable long itemId,
                                 @RequestBody @Valid CreateCommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
