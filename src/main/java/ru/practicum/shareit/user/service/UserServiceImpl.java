package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private final UserMapper userMapper;

    public UserDto createUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);
        return userMapper.userToUserDto(repository.save(user));
    }

    public List<UserDto> getAllUsers() {
        return repository.findAll().stream().map(userMapper::userToUserDto).collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        return userMapper.userToUserDto(
                repository.findById(id).orElseThrow(() -> new NotFoundException("Юзера с таким id не найдено")));
    }

    public void deleteUserById(Long id) {
        repository.deleteById(id);
    }

    public UserDto updateUserById(UserDto user, Long id) {
        User userFromTable = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Юзера с таким id не найдено"));

        if (user.getName() != null && !user.getName().isBlank()) {
            userFromTable.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            userFromTable.setEmail(user.getEmail());
        }

        return userMapper.userToUserDto(repository.saveAndFlush(userFromTable));
    }
}
