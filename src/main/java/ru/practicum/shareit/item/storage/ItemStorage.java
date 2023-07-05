package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item addItemToStorage(Item item);

    Item getItemFromStorage(Integer id);

    Item deleteItemFromStorage(Integer id);

    Item updateItem(Item item);

    List<Item> getAllItemByUserId(Integer userId);

    List<Item> searchByDescription(String text);
}
