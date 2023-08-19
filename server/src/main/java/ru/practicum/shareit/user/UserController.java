package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.info("Получен запрос на создание пользователя");
        return userService.createUser(userDto);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    public UserDto getUserById(@PathVariable Long id) {
        log.info("Получен запрос на получение пользователя по id");
        return userService.getUserById(id);
    }

    @DeleteMapping("{id}")
    public void deleteUserById(@PathVariable Long id) {
        log.info("Получен запрос на удаление пользователя по id");
        userService.deleteUserById(id);
        log.info("Пользователь с id {} успешно удалён", id);
    }

    @PatchMapping("{id}")
    public UserDto updateUserById(@RequestBody UserDto user, @PathVariable Long id) {
        log.info("Получен запрос на обновление пользователя по id");
        return userService.updateUserById(user, id);
    }
}
