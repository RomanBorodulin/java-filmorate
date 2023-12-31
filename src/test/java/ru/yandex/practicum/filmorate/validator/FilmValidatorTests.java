package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class FilmValidatorTests {

    private Film film;
    private InMemoryFilmStorage filmService;
    private final FilmValidator filmValidator = new FilmValidator();

    @BeforeEach
    public void beforeEach() {

        filmService = new InMemoryFilmStorage();
        film = Film.builder().name("Harry Potter and the Philosopher's Stone")
                .description("The boy who lived")
                .releaseDate(LocalDate.of(2001, Month.NOVEMBER, 4))
                .duration(152)
                .likes(new HashSet<>())
                .genres(new HashSet<>())
                .build();
    }

    @Test
    public void shouldNotAddFilmWhenNameIsEmpty() {
        film.setName("");
        assertThrows(ValidationException.class, () -> filmValidator.validateAddFilm(film));
    }

    @Test
    public void shouldAddFilmWhenNameExists() {
        film.setName("I exist");
        filmService.add(film);
        assertTrue(filmService.getAllFilms().values().contains(film));
    }

    @Test
    public void shouldNotAddFilmWhenDescriptionLengthMoreThen200() {
        film.setDescription("a".repeat(201));
        assertThrows(ValidationException.class, () -> filmValidator.validateAddFilm(film));
    }

    @Test
    public void shouldAddFilmWhenDescriptionLengthEquals200() {
        film.setDescription("a".repeat(200));
        filmService.add(film);
        assertTrue(filmService.getAllFilms().values().contains(film));
    }

    @Test
    public void shouldAddFilmWhenDescriptionLengthLess200() {
        film.setDescription("");
        filmService.add(film);
        assertTrue(filmService.getAllFilms().values().contains(film));

    }

    @Test
    public void shouldNotAddFilmWhenReleaseDateBefore28December1895() {
        film.setReleaseDate(LocalDate.of(1895, Month.DECEMBER, 27));
        assertThrows(ValidationException.class, () -> filmValidator.validateAddFilm(film));;
    }

    @Test
    public void shouldAddFilmWhenReleaseDateEquals28December1895() {
        film.setReleaseDate(LocalDate.of(1895, Month.DECEMBER, 28));
        filmService.add(film);
        assertTrue(filmService.getAllFilms().values().contains(film));
    }

    @Test
    public void shouldAddFilmWhenReleaseDateAfter28December1895() {
        film.setReleaseDate(LocalDate.of(1895, Month.DECEMBER, 29));
        filmService.add(film);
        assertTrue(filmService.getAllFilms().values().contains(film));
    }

    @Test
    public void shouldNotAddFilmWhenDurationIsNegative() {
        film.setDuration(-1);
        assertThrows(ValidationException.class, () -> filmValidator.validateAddFilm(film));
    }

    @Test
    public void shouldNotAddFilmWhenDurationIsZero() {
        film.setDuration(0);
        assertThrows(ValidationException.class, () -> filmValidator.validateAddFilm(film));
    }

    @Test
    public void shouldAddFilmWhenDurationIsPositive() {
        film.setDuration(1);
        filmService.add(film);
        assertTrue(filmService.getAllFilms().values().contains(film));
    }


}
