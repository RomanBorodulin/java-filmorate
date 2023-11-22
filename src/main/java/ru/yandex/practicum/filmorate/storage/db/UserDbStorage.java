package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        long id = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        log.debug("Новый пользователь c id={} добавлен", id);
        return getById(id);
    }

    @Override
    public User delete(long id) {
        User user = getById(id);
        String sqlQuery = "delete from users where id = ?";
        jdbcTemplate.update(sqlQuery, id);
        log.debug("Пользователь c id={} удален", id);
        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "update users set " +
                "email = ?, login = ?, name = ?, birthday = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        log.debug("Пользователь c id={} обновлен", user.getId());
        return getById(user.getId());
    }

    @Override
    public Map<Long, User> getAllUsers() {
        String sqlQuery = "select * from users";
        List<User> users = jdbcTemplate.query(sqlQuery, UserDbStorage::createUser);
        return users.stream().collect(Collectors.toMap(User::getId, user -> user));
    }

    @Override
    public User getById(long id) {
        String sqlQuery = "select * from users where id = ?";
        List<User> users = jdbcTemplate.query(sqlQuery, UserDbStorage::createUser, id);
        if (users.size() != 1) {
            throw new ObjectNotFoundException(String.format("Пользователь с id=%s не единственный", id));
        }
        return users.get(0);
    }

    @Override
    public User addFriend(long id, long friendId) {
        String sqlQuery = "merge into friends(user_id, friend_id, friendship) " +
                "values (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, id, friendId, true);
        log.debug("Пользователь c id={} добавлен в друзья", friendId);
        return getById(id);
    }

    @Override
    public User deleteFriend(long id, long friendId) {
        String sqlQuery = "delete from friends where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlQuery, id, friendId);
        log.debug("Пользователь c id={} удален из друзей", friendId);
        return getById(id);
    }

    @Override
    public List<User> getFriends(long id) {
        String sqlQuery = "select u.* from friends as f join users as u on f.friend_id=u.id " +
                "where f.user_id = ?";
        return jdbcTemplate.query(sqlQuery, UserDbStorage::createUser, id);
    }

    @Override
    public List<User> getCommonFriends(long id, long friendId) {
        String sqlQuery = "select u.* from friends as f join users as u on f.friend_id=u.id " +
                "where f.user_id = ? or f.user_id = ? group by f.friend_id having count(f.friend_id) > 1";
        return jdbcTemplate.query(sqlQuery, UserDbStorage::createUser, id, friendId);
    }

    static User createUser(ResultSet rS, int rowNum) throws SQLException {
        return User.builder()
                .id(rS.getLong("id"))
                .email(rS.getString("email"))
                .login(rS.getString("login"))
                .name(rS.getString("name"))
                .birthday(rS.getDate("birthday").toLocalDate())
                .build();
    }
}
