package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAll() {
        String sqlQuery = "select * from ratings";
        return jdbcTemplate.query(sqlQuery, MpaDbStorage::createMpa);
    }

    @Override
    public Mpa getById(Integer id) {
        String sqlQuery = "select * from ratings where id = ?";
        List<Mpa> mpa = jdbcTemplate.query(sqlQuery, MpaDbStorage::createMpa, id);
        if (mpa.size() != 1) {
            throw new ObjectNotFoundException(String.format("Рейтинг с id=%s не единственный", id));
        }
        return mpa.get(0);
    }

    static Mpa createMpa(ResultSet rS, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rS.getInt("id"))
                .name(rS.getString("name"))
                .build();
    }
}
