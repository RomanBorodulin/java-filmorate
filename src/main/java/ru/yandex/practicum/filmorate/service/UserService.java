package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final UserValidator userValidator;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("filmDbStorage") FilmStorage filmStorage,
                       UserValidator userValidator) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.userValidator = userValidator;
    }

    public User add(User user) {
        userValidator.validateAddUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        return userStorage.add(user);

    }

    public User delete(long id) {
        validateExist(id);
        for (Film film : filmStorage.getAllFilms().values()) {
            if (film.getLikes().contains(id)) {
                filmStorage.deleteLike(film.getId(), id);
            }
        }
        for (User user : userStorage.getAllUsers().values()) {
            if (user.getFriends().contains(id)) {
                userStorage.deleteFriend(user.getId(), id);
            }
        }
        return userStorage.delete(id);

    }

    public User update(User user) {
        validateExist(user.getId());
        userValidator.validateAddUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setFriends(userStorage.getAllUsers().get(user.getId()).getFriends());
        return userStorage.update(user);

    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers().values();

    }

    public User getById(long id) {
        validateExist(id);
        return userStorage.getById(id);

    }


    public User addFriend(long id, long friendId) {
        validateExist(id);
        validateExist(friendId);
        return userStorage.addFriend(id, friendId);
    }

    public User deleteFriend(long id, long friendId) {
        validateExist(id);
        validateExist(friendId);
        return userStorage.deleteFriend(id, friendId);
    }

    public List<User> getFriends(long id) {
        validateExist(id);
        return userStorage.getFriends(id);

    }

    public List<User> getCommonFriends(long id, long friendId) {
        validateExist(id);
        validateExist(friendId);
        return userStorage.getCommonFriends(id, friendId);

    }

    private void validateExist(long id) {
        if (!userStorage.getAllUsers().containsKey(id)) {
            log.warn("Пользователь с id={} не существует", id);
            throw new ObjectNotFoundException("Пользователь с указанным id=" + id + " не был добавлен ранее");
        }
    }

}
