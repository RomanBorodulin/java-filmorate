package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> idToUsers = new HashMap<>();
    private static long id = 0;

    @Override
    public User add(User user) {
        user.setId(++id);
        idToUsers.put(user.getId(), user);
        log.debug("Новый пользователь c id={} добавлен", user.getId());
        return user;
    }

    @Override
    public User delete(long id) {
        log.debug("Пользователь c id={} удален", id);
        return idToUsers.remove(id);
    }

    @Override
    public User update(User user) {
        idToUsers.put(user.getId(), user);
        log.debug("Пользователь c id={} обновлен", user.getId());
        return user;
    }

    @Override
    public Map<Long, User> getAllUsers() {
        return new HashMap<>(idToUsers);
    }

    @Override
    public User getById(long id) {
        User user = idToUsers.get(id);
        return User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .friends(user.getFriends()).build();
    }

    @Override
    public User addFriend(long id, long friendId) {
        idToUsers.get(id).addFriend(friendId);
        idToUsers.get(friendId).addFriend(id);
        log.debug("Пользователь c id={} добавлен в друзья", friendId);
        return idToUsers.get(friendId);
    }

    @Override
    public User deleteFriend(long id, long friendId) {
        idToUsers.get(id).removeFriendById(friendId);
        idToUsers.get(friendId).removeFriendById(id);
        log.debug("Пользователь c id={} удален из друзей", friendId);
        return idToUsers.get(friendId);
    }

    @Override
    public List<User> getFriends(long id) {
        return idToUsers.get(id).getFriends().stream().map(this::getById).collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(long id, long friendId) {
        Set<Long> set = new HashSet<>(idToUsers.get(id).getFriends());
        set.retainAll(idToUsers.get(friendId).getFriends());
        return set.stream().map(this::getById).collect(Collectors.toList());
    }
}
