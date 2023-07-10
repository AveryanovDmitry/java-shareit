package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping
    public UserDto createUser(@Validated(UserDto.Create.class) @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    public UserDto getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @DeleteMapping("{id}")
    public UserDto deleteUserById(@PathVariable Integer id) {
        return userService.deleteUserById(id);
    }

    @PatchMapping("{id}")
    public UserDto updateUserById(@Validated(UserDto.Update.class) @RequestBody UserDto user,
                                  @PathVariable Integer id) {
        return userService.updateUserById(user, id);
    }
}
