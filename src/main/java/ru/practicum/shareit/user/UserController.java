package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping
    public UserDto createUser(@Validated(UserDto.Create.class) @RequestBody UserDto userDto) {
        log.info("Получен запрос на создание пользователя");
        return userService.createUser(userDto);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    public UserDto getUserById(@PathVariable Integer id) {
        log.info("Получен запрос на получение пользователя по id");
        return userService.getUserById(id);
    }

    @DeleteMapping("{id}")
    public UserDto deleteUserById(@PathVariable Integer id) {
        log.info("Получен запрос на удаление пользователя по id");
        return userService.deleteUserById(id);
    }

    @PatchMapping("{id}")
    public UserDto updateUserById(@Validated(UserDto.Update.class) @RequestBody UserDto user,
                                  @PathVariable Integer id) {
        log.info("Получен запрос на обновление пользователя по id");
        return userService.updateUserById(user, id);
    }
}
