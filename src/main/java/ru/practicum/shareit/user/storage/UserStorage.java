package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

public interface UserStorage {
    User addUserToStorage(User user);

    User getUserById(Integer id);

    User deleteUserById(Integer id);

    User updateUserById(User user, Integer id);


}
