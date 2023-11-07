package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    User add(User user);

    User delete(long id);

    User update(User user);

    Map<Long, User> getAllUsers();

    User getById(long id);

    User addFriend(long id, long friendId);

    User deleteFriend(long id, long friendId);

    List<User> getFriends(long id);

    List<User> getCommonFriends(long id, long friendId);


}
