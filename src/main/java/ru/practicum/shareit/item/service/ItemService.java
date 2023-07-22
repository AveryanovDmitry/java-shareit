package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long ownerId);

    ItemDto getItemFromStorage(Long id, Long userId);

    List<ItemDto> getAllItemFromStorageByUserId(Long userId);

    void deleteItemFromStorage(Long id);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    List<ItemDto> searchItemsByText(String text);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
