package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserDbStorageTest {
    private final User user1 = User.builder()
            .id(1L).email("user@email.ru").login("vanya123").name("Ivan Petrov")
            .birthday(LocalDate.of(1990, 1, 1)).build();
    private final User user2 = new User(2L, "friend@mail.ru", "friend", "friend adipisicing",
            LocalDate.of(1976, 8, 20), null);

    private final UserStorage userStorage;

    @Autowired
    public UserDbStorageTest(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Test
    public void testFindUserById() {
        userStorage.add(user1);
        User savedUser = userStorage.getById(1L);
        assertThat(savedUser).isNotNull().usingRecursiveComparison().isEqualTo(user1);
    }

    @Test
    public void testFindAllUsers() {
        userStorage.add(user1);
        userStorage.add(user2);
        Collection<User> users = userStorage.getAllUsers().values();
        assertThat(users).hasSize(2);
    }

    @Test
    public void testAddUser() {
        userStorage.add(user1);
        Map<Long, User> users = userStorage.getAllUsers();
        User savedUser = users.get(1L);
        assertThat(savedUser.getEmail()).isEqualTo(user1.getEmail());
        assertThat(savedUser.getLogin()).isEqualTo(user1.getLogin());
        assertThat(savedUser.getName()).isEqualTo(user1.getName());
        assertThat(savedUser.getBirthday()).isEqualTo(user1.getBirthday());
    }

    @Test
    public void testUpdateUser() {
        userStorage.add(user1);
        User updateUser = new User(1L, "friend@mail.ru", "update", "update",
                LocalDate.of(1976, 8, 20), null);
        userStorage.update(updateUser);
        User savedUser = userStorage.getAllUsers().get(1L);
        assertThat(savedUser).isEqualTo(updateUser);
    }

    @Test
    public void testDeleteUser() {
        userStorage.add(user1);
        userStorage.delete(1L);
        assertThat(userStorage.getAllUsers()).isEmpty();
    }

}
