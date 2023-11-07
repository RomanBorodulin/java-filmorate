package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {
    Film add(Film film);

    Film delete(long id);

    Film update(Film film);

    Map<Long, Film> getAllFilms();

    Film getById(long id);

    Film addLike(long filmId, long userId);

    Film deleteLike(long filmId, long userId);

    List<Film> getPopularFilms(int count);


}
