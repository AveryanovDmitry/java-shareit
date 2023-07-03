package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exeptions.EmailExeption;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserStorageImpl implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    private int id = 0;

    public User addUserToStorage(User user) {
        checkEmail(user);
        user.setId(++id);
        users.put(id, user);
        return users.get(id);
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public User getUserById(Integer id) {
        return users.get(id);
    }

    public User deleteUserById(Integer id) {
        return users.remove(id);
    }

    public User updateUserById(User user, Integer id) {
        user.setId(id);
        checkEmail(user);
        User updateUser = users.get(id);
        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updateUser.setEmail(user.getEmail());
        }
        return users.get(id);
    }

    private void checkEmail(User user) {
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()) && u.getId() != user.getId())) {
            throw new EmailExeption("Пользователь с такой почтой уже существует");
        }
    }
}
