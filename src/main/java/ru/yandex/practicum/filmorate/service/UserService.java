package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final Map<Integer, User> idToUsers = new HashMap<>();
    private static int id = 0;
    private final UserValidator userValidator;

    public User add(User user) throws ValidationException {
        userValidator.validateAddUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(++id);
        idToUsers.put(user.getId(), user);
        log.debug("Новый пользователь c id={} добавлен", user.getId());
        return user;
    }

    public User update(User user) throws ValidationException {
        if (!idToUsers.containsKey(user.getId())) {
            log.warn("Пользователь с id={} не существует", user.getId());
            throw new ValidationException("Пользователь с указанным id не был добавлен ранее");
        }
        userValidator.validateAddUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        idToUsers.put(user.getId(), user);
        log.debug("Пользователь c id={} обновлен", user.getId());
        return user;
    }

    public Collection<User> getAll() {
        return idToUsers.values();
    }
}
