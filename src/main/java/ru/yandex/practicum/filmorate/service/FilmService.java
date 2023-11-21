package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
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

    private final GenreStorage genreStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       FilmValidator filmValidator, GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmValidator = filmValidator;
        this.genreStorage = genreStorage;
    }

    public Film add(Film film) {
        filmValidator.validateAddFilm(film);
        validateExistGenresAndLikes(film);
        film = filmStorage.add(film);
        genreStorage.addFilmGenres(film);
        return film;
    }

    public Film delete(long id) {
        validateExistFilm(id);
        return filmStorage.delete(id);

    }

    public Film update(Film film) {
        validateExistFilm(film.getId());
        validateExistGenresAndLikes(film);
        filmValidator.validateAddFilm(film);
        genreStorage.deleteFilmGenres(film);
        film = filmStorage.update(film);
        genreStorage.addFilmGenres(film);
        return film;
    }

    public Collection<Film> getAllFilms() {
        Collection<Film> films = filmStorage.getAllFilms().values();
        films.forEach(this::validateExistGenresAndLikes);
        films.forEach(film -> {
            genreStorage.getFilmGenres(film.getId()).forEach(film::addGenre);
            filmStorage.getLikes(film.getId()).forEach(film::addLike);
        });
        return films;

    }

    public Film getById(long id) {
        validateExistFilm(id);
        Film film = filmStorage.getById(id);
        genreStorage.getFilmGenres(id).forEach(film::addGenre);
        filmStorage.getLikes(id).forEach(film::addLike);
        return film;

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
        List<Film> films = filmStorage.getPopularFilms(count);
        films.forEach(this::validateExistGenresAndLikes);
        films.forEach(film -> {
            genreStorage.getFilmGenres(film.getId()).forEach(film::addGenre);
            filmStorage.getLikes(film.getId()).forEach(film::addLike);
        });
        return films;
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

    private void validateExistGenresAndLikes(Film film) {
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        if (film.getGenres() == null) {
            film.setGenres(new HashSet<>());
        }
    }


}
