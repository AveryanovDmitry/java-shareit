package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(OWNER_ID) Integer ownerId) {
        log.info("Получен запрос создания новой вещи");
        return itemService.createItem(itemDto, ownerId);
    }

    @GetMapping("{id}")
    public ItemDto getItemByID(@PathVariable Integer id) {
        log.info("Получен запрос получения вещи по id");
        return itemService.getItemFromStorage(id);
    }

    @DeleteMapping("{id}")
    public ItemDto deleteItemById(@PathVariable Integer id) {
        log.info("Получен запрос удаления вещи по id");
        return itemService.deleteItemFromStorage(id);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @PathVariable Integer itemId,
                              @RequestHeader(OWNER_ID) Integer userId) {
        log.info("Получен запрос обновления вещи по id");
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping()
    public List<ItemDto> getItemByIdOwner(@RequestHeader(OWNER_ID) Integer id) {
        log.info("Получен запрос получения вещи по id владельца");
        return itemService.getAllItemFromStorageByUserId(id);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam(name = "text") String text) {
        log.info("Получен запрос поиска вещи по тексту");
        return itemService.searchItemsByText(text);
    }
}
