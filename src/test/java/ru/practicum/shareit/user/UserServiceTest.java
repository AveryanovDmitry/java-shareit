package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class UserServiceTest {
    private final UserService userService;
    private UserDto userTest;
    private UserDto userTestSecond;
    private UserDto updateUser;

    @BeforeEach
    public void setUp() {
        userTest = UserDto.builder()
                .name("test1")
                .email("test@test.ru")
                .build();
        userTestSecond = UserDto.builder()
                .name("test2")
                .email("test2@test.ru")
                .build();
    }

    @Test
    void createAndGetUser() {
        UserDto savedUser = userService.createUser(userTest);
        UserDto findUser = userService.getUserById(1L);
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(findUser);
    }

    @Test
    void createUserWithDuplicateEmail() {
        userService.createUser(userTest);
        assertThatThrownBy(
                () -> userService.createUser(userTest))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void getNotExistUserById() {
        assertThatThrownBy(
                () -> userService.getUserById(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getEmptyUsersList() {
        assertThat(userService.getAllUsers()).isEmpty();
    }

    @Test
    void getUsersList() {
        UserDto savedUser1 = userService.createUser(userTest);
        UserDto savedUser2 = userService.createUser(userTestSecond);
        List<UserDto> findUsers = userService.getAllUsers();
        assertThat(findUsers).element(0).usingRecursiveComparison().isEqualTo(savedUser1);
        assertThat(findUsers).element(1).usingRecursiveComparison().isEqualTo(savedUser2);
    }

    @Test
    void updateUser() {
        updateUser = UserDto.builder()
                .name("update name")
                .email("update-email@test.ru")
                .build();
        userService.createUser(userTest);
        userService.updateUserById(updateUser, 1L);
        UserDto updatedUser1 = userService.getUserById(1L);
        assertThat(updatedUser1.getName()).isEqualTo(updateUser.getName());
        assertThat(updatedUser1.getEmail()).isEqualTo(updateUser.getEmail());
    }

    @Test
    void updateUserName() {
        updateUser = UserDto.builder()
                .email("update-email@test.ru")
                .build();
        userService.createUser(userTest);
        userService.updateUserById(updateUser, 1L);
        UserDto updatedUser1 = userService.getUserById(1L);
        assertThat(updatedUser1.getName()).isEqualTo(userTest.getName());
        assertThat(updatedUser1.getEmail()).isEqualTo(updatedUser1.getEmail());
    }

    @Test
    void updateUserEmail() {
        updateUser = UserDto.builder()
                .name("update name")
                .build();
        userService.createUser(userTest);
        userService.updateUserById(updateUser, 1L);
        var updatedUser1 = userService.getUserById(1L);
        assertThat(updatedUser1.getName()).isEqualTo(updateUser.getName());
        assertThat(updatedUser1.getEmail()).isEqualTo(userTest.getEmail());
    }

    @Test
    void deleteUserById() {
        UserDto savedUser = userService.createUser(userTest);
        userService.deleteUserById(savedUser.getId());
        assertThatThrownBy(() -> userService.getUserById(savedUser.getId())).isInstanceOf(NotFoundException.class);
    }
}