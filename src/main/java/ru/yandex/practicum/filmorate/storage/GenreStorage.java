package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Configuration
public interface GenreStorage {

    List<Genre> getAll();

    Genre getById(Integer id);

    List<Genre> getFilmGenres(Long filmId);

    void addFilmGenres(Film film);

    void deleteFilmGenres(Film film);
}
