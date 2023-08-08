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

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private static final String OWNER_ID = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Validated(CreateUpdateItemDto.Create.class) @RequestBody CreateUpdateItemDto itemDto,
                              @RequestHeader(OWNER_ID) @Min(1) Long ownerId) {
        log.info("Получен запрос создания новой вещи");
        return itemService.createItem(itemDto, ownerId);
    }

    @GetMapping("{id}")
    public ItemDto getItemByID(@PathVariable @Min(1) Long id, @RequestHeader(OWNER_ID) @Min(1) long userId) {
        log.info("Получен запрос получения вещи по id");
        return itemService.getItemFromStorage(id, userId);
    }

    @DeleteMapping("{id}")
    public void deleteItemById(@PathVariable @Min(1) Long id) {
        log.info("Получен запрос удаления вещи по id");
        itemService.deleteItemFromStorage(id);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Validated(CreateUpdateItemDto.Update.class) @RequestBody CreateUpdateItemDto itemDto,
                              @PathVariable @Min(1) Long itemId,
                              @RequestHeader(OWNER_ID) @Min(1) Long userId) {
        log.info("Получен запрос обновления вещи по id");
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping()
    public List<ItemDto> getItemByIdOwner(@RequestHeader(OWNER_ID) @Min(1) Long id,
                                          @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                          @RequestParam(defaultValue = "10") @Min(1) @Max(20) Integer size) {
        log.info("Получен запрос получения вещи по id владельца");
        return itemService.getAllItemFromStorageByUserId(PageRequest.of(from / size, size), id);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam(name = "text") String text,
                                           @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                           @RequestParam(defaultValue = "10") @Min(1) @Max(20) Integer size) {
        log.info("Получен запрос поиска вещи по тексту");
        return itemService.searchItemsByText(PageRequest.of(from / size, size), text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(OWNER_ID) @Min(1) long userId,
                                 @PathVariable @Min(1) long itemId,
                                 @RequestBody @Valid CreateCommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
