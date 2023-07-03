package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ItemStorageImpl {
    private int id = 1;
    Map<Integer, Item> items = new HashMap<>();

    public Item addItemToStorage(Item item) {
        item.setId(++id);
        items.put(item.getId(), item);
        return item;
    }
}
