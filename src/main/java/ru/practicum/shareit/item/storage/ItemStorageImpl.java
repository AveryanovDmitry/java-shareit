package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemStorageImpl implements ItemStorage {
    private int id = 0;
    private final Map<Integer, Item> items = new HashMap<>();

    private final Map<Integer, List<Item>> userItemIndex = new LinkedHashMap<>();

    public Item addItemToStorage(Item item) {
        item.setId(++id);
        items.put(item.getId(), item);
        userItemIndex.computeIfAbsent(item.getOwner().getId(), k -> new ArrayList<>()).add(item);
        return item;
    }

    public Item getItemFromStorage(Integer id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Вещи с таким id не найдено");
        }
        return items.get(id);
    }

    public Item deleteItemFromStorage(Integer id) {
        return items.remove(id);
    }

    public Item updateItem(Item item) {
        Item mutableItem = items.get(item.getId());
        if (item.getName() != null && !item.getName().isBlank()) {
            mutableItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            mutableItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            mutableItem.setAvailable(item.getAvailable());
        }
        return mutableItem;
    }

    public List<Item> getAllItemByUserId(Integer userId) {
        return userItemIndex.get(userId);
    }

    public List<Item> searchByText(String text) {
        return items.values().stream()
                .filter(item -> item.getAvailable() && (item.getDescription().toLowerCase().contains(text)
                        || item.getName().toLowerCase().contains(text)))
                .collect(Collectors.toList());
    }
}
