package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    private final FilmValidator filmValidator;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       FilmValidator filmValidator) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmValidator = filmValidator;
    }

    public Film add(Film film) {
        filmValidator.validateAddFilm(film);
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        if (film.getGenres() == null) {
            film.setGenres(new HashSet<>());
        }
        return filmStorage.add(film);
    }

    public Film delete(long id) {
        validateExistFilm(id);
        return filmStorage.delete(id);

    }

    public Film update(Film film) {
        validateExistFilm(film.getId());
        filmValidator.validateAddFilm(film);
        return filmStorage.update(film);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms().values();

    }

    public Film getById(long id) {
        validateExistFilm(id);
        return filmStorage.getById(id);

    }

    public Film addLike(long filmId, long userId) {
        validateExistFilm(filmId);
        validateExistUser(userId);
        return filmStorage.addLike(filmId, userId);

    }

    public Film deleteLike(long filmId, long userId) {
        validateExistFilm(filmId);
        validateExistUser(userId);
        return filmStorage.deleteLike(filmId, userId);

    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    private void validateExistFilm(long id) {
        if (!filmStorage.getAllFilms().containsKey(id)) {
            log.warn("Фильм с id={} не существует", id);
            throw new ObjectNotFoundException("Фильм с указанным id=" + id + " не был добавлен ранее");
        }
    }

    private void validateExistUser(long id) {
        if (!userStorage.getAllUsers().containsKey(id)) {
            log.warn("Пользователь с id={} не существует", id);
            throw new ObjectNotFoundException("Пользователь с указанным id=" + id + " не был добавлен ранее");
        }
    }


}
