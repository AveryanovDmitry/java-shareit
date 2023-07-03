package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorageImpl;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl {
    private final ItemStorageImpl itemStorage;
    private final ItemMapper itemMapper;

    public ItemDto createItem(ItemDto itemDto, Integer ownerId) {
        Item item = itemMapper.itemDtoToItem(itemDto);
        return itemMapper.itemToItemDto(itemStorage.addItemToStorage(item));
    }
}
