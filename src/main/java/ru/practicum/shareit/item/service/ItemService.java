package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Integer ownerId);

    ItemDto getItemFromStorage(Integer id);

    List<ItemDto> getAllItemFromStorageByUserId(Integer userId);

    ItemDto deleteItemFromStorage(Integer id);

    ItemDto updateItem(ItemDto itemDto, Integer itemId, Integer userId);

    List<ItemDto> searchItemsByDescription(String text);
}
