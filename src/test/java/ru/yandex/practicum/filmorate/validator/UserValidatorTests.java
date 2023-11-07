package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UserValidatorTests {
    private User user;
    private UserService userService;

    @BeforeEach
    public void beforeEach() {
        userService = new UserService(new InMemoryUserStorage(), new InMemoryFilmStorage(), new UserValidator());
        user = User.builder().email("email@ya.ru")
                .login("User12345")
                .name("User")
                .birthday(LocalDate.of(2000, Month.APRIL, 7)).build();
    }

    @Test
    public void shouldAddUserWhenEmailIsValid() {
        userService.add(user);
        assertTrue(userService.getAllUsers().contains(user));

    }

    @Test
    public void shouldNotAddUserWhenEmailIsEmpty() {
        user.setEmail("");
        assertThrows(ValidationException.class, () -> userService.add(user));
        assertTrue(userService.getAllUsers().isEmpty());
    }

    @Test
    public void shouldNotAddUserWhenEmailDoesNotContainSymbol() {
        user.setEmail("email.ya.ru");
        assertThrows(ValidationException.class, () -> userService.add(user));
        assertTrue(userService.getAllUsers().isEmpty());
    }

    @Test
    public void shouldAddUserWhenLoginIsValid() {
        user.setLogin("ValidLogin");
        userService.add(user);
        assertTrue(userService.getAllUsers().contains(user));
    }

    @Test
    public void shouldNotAddUserWhenLoginIsEmpty() {
        user.setLogin(" ");
        assertThrows(ValidationException.class, () -> userService.add(user));
        assertTrue(userService.getAllUsers().isEmpty());
    }

    @Test
    public void shouldNotAddUserWhenLoginContainsSpaces() {
        user.setLogin("Contains Spaces");
        assertThrows(ValidationException.class, () -> userService.add(user));
        assertTrue(userService.getAllUsers().isEmpty());
    }

    @Test
    public void shouldAddUserWhenNameIsEmptyAndNameWillBeEqualToLogin() {
        user.setName(" ");
        userService.add(user);
        assertEquals(user.getLogin(), new ArrayList<>(userService.getAllUsers()).get(0).getName());
    }

    @Test
    public void shouldAddUserWhenNameIsNullAndNameWillBeEqualToLogin() {
        user.setName(null);
        userService.add(user);
        assertEquals(user.getLogin(), new ArrayList<>(userService.getAllUsers()).get(0).getName());
    }

    @Test
    public void shouldAddUserWhenNameIsValid() {
        user.setName("ValidName");
        userService.add(user);
        assertTrue(userService.getAllUsers().contains(user));
    }

    @Test
    public void shouldNotAddUserWhenBirthdayInTheFuture() {
        user.setBirthday(LocalDate.of(2200, Month.APRIL, 12));
        assertThrows(ValidationException.class, () -> userService.add(user));
        assertTrue(userService.getAllUsers().isEmpty());
    }

    @Test
    public void shouldAddUserWhenBirthdayNow() {
        user.setBirthday(LocalDate.now());
        userService.add(user);
        assertTrue(userService.getAllUsers().contains(user));
    }

    @Test
    public void shouldAddUserWhenBirthdayInThePast() {
        user.setBirthday(LocalDate.of(2000, Month.APRIL, 12));
        userService.add(user);
        assertTrue(userService.getAllUsers().contains(user));
    }
}
