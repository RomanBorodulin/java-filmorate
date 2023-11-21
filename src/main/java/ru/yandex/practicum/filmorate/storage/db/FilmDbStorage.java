package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film add(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        long id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        film.setId(id);
        log.debug("Новый фильм c id={} добавлен", id);
        return film;
    }

    @Override
    public Film delete(long id) {
        Film film = getById(id);
        String sqlQuery = "delete from films where id = ?";
        jdbcTemplate.update(sqlQuery, id);
        log.debug("Фильм с id={} удален", id);
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "update films set " +
                "name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        log.debug("Фильм c id={} обновлен", film.getId());
        return film;
    }

    @Override
    public Map<Long, Film> getAllFilms() {
        String sqlQuery = "select f.*, r.name as mpa_name from films as f " +
                "join ratings as r on f.rating_id=r.id";
        List<Film> films = jdbcTemplate.query(sqlQuery, FilmDbStorage::createFilm);
        return films.stream().collect(Collectors.toMap(Film::getId, film -> film));
    }

    @Override
    public Film getById(long id) {
        String sqlQuery = "select f.*, r.name as mpa_name from films as f " +
                "join ratings as r on f.rating_id=r.id where f.id = ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, FilmDbStorage::createFilm, id);
        if (films.size() != 1) {
            throw new ObjectNotFoundException(String.format("Фильм с id=%s не единственный", id));
        }
        Film film = films.get(0);
        validateExistGenresAndLikes(film);
        return film;
    }

    public List<Long> getLikes(long filmId) {
        String sqlQuery = "select user_id from likes where film_id = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, filmId);
    }

    @Override
    public Film addLike(long filmId, long userId) {
        String sqlQuery = "insert into likes(film_id, user_id) " +
                "values(?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        log.debug("Лайк от пользователя с id={} добавлен", userId);
        return getById(filmId);

    }

    @Override
    public Film deleteLike(long filmId, long userId) {
        String sqlQuery = "delete from likes where film_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        log.debug("Лайк от пользователя с id={} удален", userId);
        return getById(filmId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sqlQuery = "select f.*, r.name as mpa_name from films as f " +
                "join ratings as r on f.rating_id=r.id " +
                "left join likes as l on f.id=l.film_id " +
                "group by f.id order by count(l.user_id) desc limit ?";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::createFilm, count);
    }

    private void validateExistGenresAndLikes(Film film) {
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        if (film.getGenres() == null) {
            film.setGenres(new HashSet<>());
        }
    }

    static Film createFilm(ResultSet rS, int rowNum) throws SQLException {
        return Film.builder()
                .id(rS.getLong("id"))
                .name(rS.getString("name"))
                .description(rS.getString("description"))
                .releaseDate(rS.getDate("release_date").toLocalDate())
                .duration(rS.getInt("duration"))
                .mpa(new Mpa(rS.getInt("rating_id"), rS.getString("mpa_name")))
                .build();
    }
}
