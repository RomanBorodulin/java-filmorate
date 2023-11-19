package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
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

    static Genre createGenre(ResultSet rS, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rS.getInt("id"))
                .name(rS.getString("name"))
                .build();
    }
}
