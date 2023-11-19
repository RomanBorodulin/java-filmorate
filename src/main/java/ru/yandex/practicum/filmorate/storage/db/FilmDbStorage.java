package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
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
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sqlQuery = "insert into film_genres(film_id, genre_id) " +
                    "values (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlQuery, id, genre.getId());
            }
        }
        log.debug("Новый фильм c id={} добавлен", id);
        return getById(id);
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
        jdbcTemplate.update("delete from film_genres where film_id = ?", film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            sqlQuery = "insert into film_genres(film_id, genre_id) " +
                    "values (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
            }
        }
        return getById(film.getId());
    }

    @Override
    public Map<Long, Film> getAllFilms() {
        String sqlQuery = "select * from films";
        List<Film> films = jdbcTemplate.query(sqlQuery, FilmDbStorage::createFilm);
        return films.stream().collect(Collectors.toMap(Film::getId, film -> film));
    }

    @Override
    public Film getById(long id) {
        String sqlQuery = "select * from films where id = ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, FilmDbStorage::createFilm, id);
        if (films.size() != 1) {
            throw new ObjectNotFoundException(String.format("Фильм с id=%s не единственный", id));
        }
        Film film = films.get(0);
        sqlQuery = "select g.id from film_genres as fg join genres as g on g.id=fg.genre_id "
                + "where fg.film_id = ?";
        List<Integer> genres = jdbcTemplate.queryForList(sqlQuery, Integer.class, id);
        if (!genres.isEmpty()) {
            film.setGenres(new HashSet<>());
            for (Integer genre : genres) {
                film.addGenre(new Genre(genre, null));
            }
        }
        sqlQuery = "select user_id from likes where film_id = ?";
        List<Long> likes = jdbcTemplate.queryForList(sqlQuery, Long.class, id);
        if (!likes.isEmpty()) {
            film.setLikes(new HashSet<>());
            for (Long like : likes) {
                film.addLike(like);
            }
        }
        return film;
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
        String sqlQuery = "select f.* from films as f left join likes as l on f.id=l.film_id " +
                "group by f.id order by count(l.user_id) desc limit ?";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::createFilm, count);
    }

    static Film createFilm(ResultSet rS, int rowNum) throws SQLException {
        return Film.builder()
                .id(rS.getLong("id"))
                .name(rS.getString("name"))
                .description(rS.getString("description"))
                .releaseDate(rS.getDate("release_date").toLocalDate())
                .duration(rS.getInt("duration"))
                .mpa(MpaDbStorage.createMpa(rS, rowNum))
                .build();
    }
}
