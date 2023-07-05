package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemStorageImpl implements ItemStorage {
    private int id = 0;
    Map<Integer, Item> items = new HashMap<>();

    public Item addItemToStorage(Item item) {
        item.setId(++id);
        items.put(item.getId(), item);
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
        Item oldItem = items.get(item.getId());
        if (item.getName() == null) {
            item.setName(oldItem.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(oldItem.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(oldItem.getAvailable());
        }
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    public List<Item> getAllItemByUserId(Integer userId) {
        return items.values().stream().filter(item -> item.getOwner().getId() == userId).collect(Collectors.toList());
    }

    public List<Item> searchByDescription(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return items.values().stream()
                .filter(item -> item.getDescription().toLowerCase().contains(text) && item.getAvailable())
                .collect(Collectors.toList());
    }
}
