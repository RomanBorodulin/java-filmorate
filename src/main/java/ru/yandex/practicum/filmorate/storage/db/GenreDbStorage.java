package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAll() {
        String sqlQuery = "select * from genres";
        return jdbcTemplate.query(sqlQuery, GenreDbStorage::createGenre);
    }

    @Override
    public Genre getById(Integer id) {
        String sqlQuery = "select * from genres where id = ?";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, GenreDbStorage::createGenre, id);
        if (genres.size() != 1) {
            throw new ObjectNotFoundException(String.format("Жанр с id=%s не единственный", id));
        }
        return genres.get(0);
    }

    @Override
    public List<Genre> getFilmGenres(Long filmId) {
        String sqlQuery = "select g.* from film_genres as fg join genres as g on g.id=fg.genre_id "
                + "where fg.film_id = ?";
        return jdbcTemplate.query(sqlQuery, GenreDbStorage::createGenre, filmId);
    }

    @Override
    public void addFilmGenres(Film film) {
        String sqlQuery = "insert into film_genres(film_id, genre_id) " +
                "values (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
            genre.setName(getById(genre.getId()).getName());
        }

    }

    @Override
    public void deleteFilmGenres(Film film) {
        String sqlQuery = "delete from film_genres where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    static Genre createGenre(ResultSet rS, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rS.getInt("id"))
                .name(rS.getString("name"))
                .build();
    }
}
