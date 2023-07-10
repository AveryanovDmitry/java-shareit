package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeptions.EmailExeption;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserStorageImpl implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    private int id = 0;

    public User addUserToStorage(User user) {
        checkEmail(user);
        user.setId(++id);
        users.put(id, user);
        return user;
    }

    public Collection<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    public User getUserById(Integer id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователя с таким id не найдено");
        }
        return user;
    }

    public User deleteUserById(Integer id) {
        return users.remove(id);
    }

    public User updateUserById(User user, Integer id) {
        user.setId(id);
        checkEmail(user);
        User updateUser = users.get(id);
        if (user.getName() != null && !user.getName().isBlank()) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            updateUser.setEmail(user.getEmail());
        }
        return updateUser;
    }

    private void checkEmail(User user) {
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail())
                && !Objects.equals(u.getId(), user.getId()))) {
            throw new EmailExeption("Пользователь с такой почтой уже существует");
        }
    }
}
