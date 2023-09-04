package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import java.util.Collections;

import static ru.practicum.shareit.ShareItGateway.USER_ID_HEADER;

@RestController
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) @Min(1) Long userId,
                                             @Validated(CreateUpdateItemDto.Create.class)
                                             @RequestBody CreateUpdateItemDto itemDto) {
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID_HEADER) @Min(1) Long userId,
                                             @Validated(CreateUpdateItemDto.Update.class)
                                             @RequestBody CreateUpdateItemDto itemDtoUpdate,
                                             @PathVariable @Min(1) Long itemId) {
        return itemClient.updateItem(userId, itemDtoUpdate, itemId);
    }

    @GetMapping("{itemId}")
    public Object getItemByItemId(@RequestHeader(USER_ID_HEADER) @Min(1) Long userId,
                                  @PathVariable @Min(1) Long itemId) {
        return itemClient.getItemByItemId(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getPersonalItems(
            @RequestHeader(USER_ID_HEADER) @Min(1) Long userId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        return itemClient.getPersonalItems(userId, from, size);
    }

    @GetMapping("search")
    public Object getFoundItems(
            @RequestHeader(USER_ID_HEADER) @Min(1) Long userId,
            @RequestParam String text,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemClient.getFoundItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID_HEADER) @Min(1) long userId,
                                             @PathVariable @Min(1) long itemId,
                                             @RequestBody @Valid CreateCommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }

}