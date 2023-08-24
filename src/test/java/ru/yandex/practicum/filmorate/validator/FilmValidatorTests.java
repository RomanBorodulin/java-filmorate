package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class FilmValidatorTests {

    private Film film;
    private FilmService filmService;

    @BeforeEach
    public void beforeEach() {
        filmService = new FilmService(new FilmValidator());
        film = Film.builder().name("Harry Potter and the Philosopher's Stone")
                .description("The boy who lived")
                .releaseDate(LocalDate.of(2001, Month.NOVEMBER, 4))
                .duration(152).build();
    }

    @Test
    public void shouldNotAddFilmWhenNameIsEmpty() {
        film.setName("");
        assertThrows(ValidationException.class, () -> filmService.add(film));
        assertTrue(filmService.getAll().isEmpty());
    }

    @Test
    public void shouldAddFilmWhenNameExists() throws ValidationException {
        film.setName("I exist");
        filmService.add(film);
        assertTrue(filmService.getAll().contains(film));
    }

    @Test
    public void shouldNotAddFilmWhenDescriptionLengthMoreThen200() {
        film.setDescription("a".repeat(201));
        assertThrows(ValidationException.class, () -> filmService.add(film));
        assertTrue(filmService.getAll().isEmpty());
    }

    @Test
    public void shouldAddFilmWhenDescriptionLengthEquals200() throws ValidationException {
        film.setDescription("a".repeat(200));
        filmService.add(film);
        assertTrue(filmService.getAll().contains(film));
    }

    @Test
    public void shouldAddFilmWhenDescriptionLengthLess200() throws ValidationException {
        film.setDescription("");
        filmService.add(film);
        assertTrue(filmService.getAll().contains(film));

    }

    @Test
    public void shouldNotAddFilmWhenReleaseDateBefore28December1895() {
        film.setReleaseDate(LocalDate.of(1895, Month.DECEMBER, 27));
        assertThrows(ValidationException.class, () -> filmService.add(film));
        assertTrue(filmService.getAll().isEmpty());
    }

    @Test
    public void shouldAddFilmWhenReleaseDateEquals28December1895() throws ValidationException {
        film.setReleaseDate(LocalDate.of(1895, Month.DECEMBER, 28));
        filmService.add(film);
        assertTrue(filmService.getAll().contains(film));
    }

    @Test
    public void shouldAddFilmWhenReleaseDateAfter28December1895() throws ValidationException {
        film.setReleaseDate(LocalDate.of(1895, Month.DECEMBER, 29));
        filmService.add(film);
        assertTrue(filmService.getAll().contains(film));
    }

    @Test
    public void shouldNotAddFilmWhenDurationIsNegative() {
        film.setDuration(-1);
        assertThrows(ValidationException.class, () -> filmService.add(film));
        assertTrue(filmService.getAll().isEmpty());
    }

    @Test
    public void shouldNotAddFilmWhenDurationIsZero() {
        film.setDuration(0);
        assertThrows(ValidationException.class, () -> filmService.add(film));
        assertTrue(filmService.getAll().isEmpty());
    }

    @Test
    public void shouldAddFilmWhenDurationIsPositive() throws ValidationException {
        film.setDuration(1);
        filmService.add(film);
        assertTrue(filmService.getAll().contains(film));
    }


}
