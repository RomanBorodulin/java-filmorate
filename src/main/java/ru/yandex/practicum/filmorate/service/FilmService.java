package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final Map<Integer, Film> idToFilms = new HashMap<>();
    private static int id = 0;
    private final FilmValidator filmValidator;

    public Film add(Film film) {
        filmValidator.validateAddFilm(film);
        film.setId(++id);
        idToFilms.put(film.getId(), film);
        log.debug("Новый фильм с id={} добавлен", film.getId());
        return film;
    }

    public Film update(Film film) {
        if (!idToFilms.containsKey(film.getId())) {
            log.warn("Фильм с id={} не существует", film.getId());
            throw new ValidationException("Фильм с указанным id не был добавлен ранее");
        }
        filmValidator.validateAddFilm(film);
        idToFilms.put(film.getId(), film);
        log.debug("Фильм c id={} обновлен", film.getId());
        return film;
    }

    public Collection<Film> getAll() {
        return idToFilms.values();
    }
}
