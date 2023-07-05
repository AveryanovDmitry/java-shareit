package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private static final String OWNER_ID = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(OWNER_ID) Integer ownerId) {
        return itemService.createItem(itemDto, ownerId);
    }

    @GetMapping("{id}")
    public ItemDto getItemByID(@PathVariable Integer id) {
        return itemService.getItemFromStorage(id);
    }

    @DeleteMapping("{id}")
    public ItemDto deleteItemById(@PathVariable Integer id) {
        return itemService.deleteItemFromStorage(id);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @PathVariable Integer itemId,
                              @RequestHeader(OWNER_ID) Integer userId) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping()
    public List<ItemDto> updateItem(@RequestHeader(OWNER_ID) Integer id) {
        return itemService.getAllItemFromStorageByUserId(id);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam(name = "text") String text) {
        return itemService.searchItemsByDescription(text);
    }
}
