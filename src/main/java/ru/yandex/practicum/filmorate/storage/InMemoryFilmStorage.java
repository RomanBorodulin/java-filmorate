package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> idToFilms = new HashMap<>();

    @Override
    public Film add(Film film) {
        idToFilms.put(film.getId(), film);
        log.debug("Новый фильм с id={} добавлен", film.getId());
        return film;
    }

    @Override
    public Film delete(long id) {
        log.debug("Фильм с id={} удален", id);
        return idToFilms.remove(id);
    }

    @Override
    public Film update(Film film) {
        idToFilms.put(film.getId(), film);
        log.debug("Фильм c id={} обновлен", film.getId());
        return film;
    }

    @Override
    public Map<Long, Film> getAllFilms() {
        return new HashMap<>(idToFilms);
    }

    @Override
    public Film getById(long id) {
        if (!idToFilms.containsKey(id)) {
            throw new ValidationException("Фильм с указанным id=" + id + " не был добавлен ранее");
        }
        Film film = idToFilms.get(id);
        return Film.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .likes(film.getLikes()).build();
    }

    @Override
    public Film addLike(long filmId, long userId) {
        idToFilms.get(filmId).addLike(userId);
        log.debug("Лайк от пользователя с id={} добавлен", userId);
        return idToFilms.get(filmId);
    }

    @Override
    public Film deleteLike(long filmId, long userId) {
        idToFilms.get(filmId).removeLikeById(userId);
        log.debug("Лайк от пользователя с id={} удален", userId);
        return idToFilms.get(filmId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return idToFilms.values().stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count).collect(Collectors.toList());
    }
}
