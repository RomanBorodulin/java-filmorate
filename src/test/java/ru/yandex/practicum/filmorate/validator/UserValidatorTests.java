package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

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
        userService = new UserService(new UserValidator());
        user = User.builder().email("email@ya.ru")
                .login("User12345")
                .name("User")
                .birthday(LocalDate.of(2000, Month.APRIL, 7)).build();
    }

    @Test
    public void shouldAddUserWhenEmailIsValid() throws ValidationException {
        userService.add(user);
        assertTrue(userService.getAll().contains(user));

    }

    @Test
    public void shouldNotAddUserWhenEmailIsEmpty() {
        user.setEmail("");
        assertThrows(ValidationException.class, () -> userService.add(user));
        assertTrue(userService.getAll().isEmpty());
    }

    @Test
    public void shouldNotAddUserWhenEmailDoesNotContainSymbol() {
        user.setEmail("email.ya.ru");
        assertThrows(ValidationException.class, () -> userService.add(user));
        assertTrue(userService.getAll().isEmpty());
    }

    @Test
    public void shouldAddUserWhenLoginIsValid() throws ValidationException {
        user.setLogin("ValidLogin");
        userService.add(user);
        assertTrue(userService.getAll().contains(user));
    }

    @Test
    public void shouldNotAddUserWhenLoginIsEmpty() {
        user.setLogin(" ");
        assertThrows(ValidationException.class, () -> userService.add(user));
        assertTrue(userService.getAll().isEmpty());
    }

    @Test
    public void shouldNotAddUserWhenLoginContainsSpaces() {
        user.setLogin("Contains Spaces");
        assertThrows(ValidationException.class, () -> userService.add(user));
        assertTrue(userService.getAll().isEmpty());
    }

    @Test
    public void shouldAddUserWhenNameIsEmptyAndNameWillBeEqualToLogin() throws ValidationException {
        user.setName(" ");
        userService.add(user);
        assertEquals(user.getLogin(), new ArrayList<>(userService.getAll()).get(0).getName());
    }

    @Test
    public void shouldAddUserWhenNameIsNullAndNameWillBeEqualToLogin() throws ValidationException {
        user.setName(null);
        userService.add(user);
        assertEquals(user.getLogin(), new ArrayList<>(userService.getAll()).get(0).getName());
    }

    @Test
    public void shouldAddUserWhenNameIsValid() throws ValidationException {
        user.setName("ValidName");
        userService.add(user);
        assertTrue(userService.getAll().contains(user));
    }

    @Test
    public void shouldNotAddUserWhenBirthdayInTheFuture() {
        user.setBirthday(LocalDate.of(2200, Month.APRIL, 12));
        assertThrows(ValidationException.class, () -> userService.add(user));
        assertTrue(userService.getAll().isEmpty());
    }

    @Test
    public void shouldAddUserWhenBirthdayNow() throws ValidationException {
        user.setBirthday(LocalDate.now());
        userService.add(user);
        assertTrue(userService.getAll().contains(user));
    }

    @Test
    public void shouldAddUserWhenBirthdayInThePast() throws ValidationException {
        user.setBirthday(LocalDate.of(2000, Month.APRIL, 12));
        userService.add(user);
        assertTrue(userService.getAll().contains(user));
    }
}
