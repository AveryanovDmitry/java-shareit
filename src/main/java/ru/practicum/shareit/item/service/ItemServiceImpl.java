package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;

    public ItemDto createItem(ItemDto itemDto, Integer ownerId) {
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userStorage.getUserById(ownerId));
        return itemMapper.toItemDto(itemStorage.addItemToStorage(item));
    }

    public ItemDto getItemFromStorage(Integer id) {
        return itemMapper.toItemDto(itemStorage.getItemFromStorage(id));
    }

    public List<ItemDto> getAllItemFromStorageByUserId(Integer userId) {
        List<Item> items = itemStorage.getAllItemByUserId(userId);
        return items.stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    public ItemDto deleteItemFromStorage(Integer id) {
        return itemMapper.toItemDto(itemStorage.deleteItemFromStorage(id));
    }

    public ItemDto updateItem(ItemDto itemDto, Integer itemId, Integer userId) {
        userStorage.getUserById(userId);
        checkOwnerForUpdate(itemStorage.getItemFromStorage(itemId), userId);
        Item newItem = itemMapper.toItem(itemDto);
        newItem.setId(itemId);
        newItem.setOwner(userStorage.getUserById(userId));
        return itemMapper.toItemDto(itemStorage.updateItem(newItem));
    }

    private void checkOwnerForUpdate(Item oldItem, Integer userId) {
        Integer idOwnerOldItem = oldItem.getOwner().getId();
        if (!Objects.equals(idOwnerOldItem, userId)) {
            throw new NotFoundException("Пользователь не хозяин этой вещи");
        }
    }

    public List<ItemDto> searchItemsByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemStorage.searchByText(text.toLowerCase())
                .stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }
}
