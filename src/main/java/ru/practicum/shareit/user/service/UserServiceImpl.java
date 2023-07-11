package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    private final UserMapper userMapper;

    public UserDto createUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);
        return userMapper.userToUserDto(storage.addUserToStorage(user));
    }

    public List<UserDto> getAllUsers() {
        return storage.getAllUsers().stream().map(userMapper::userToUserDto).collect(Collectors.toList());
    }

    public UserDto getUserById(Integer id) {
        return userMapper.userToUserDto(storage.getUserById(id));
    }

    public UserDto deleteUserById(Integer id) {
        return userMapper.userToUserDto(storage.deleteUserById(id));
    }

    public UserDto updateUserById(UserDto user, Integer id) {
        return userMapper.userToUserDto(storage.updateUserById(userMapper.userDtoToUser(user), id));
    }
}