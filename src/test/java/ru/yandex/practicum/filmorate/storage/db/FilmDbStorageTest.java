package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmDbStorageTest {
    private final Film film1 = Film.builder().name("Harry Potter and the Philosopher's Stone")
            .description("The boy who lived")
            .releaseDate(LocalDate.of(2001, Month.NOVEMBER, 4))
            .duration(152)
            .mpa(new Mpa(3, "PG-13"))
            .likes(new HashSet<>())
            .genres(new HashSet<>())
            .build();
    private final Film film2 = Film.builder().name("Simple Film")
            .description("Very simple")
            .releaseDate(LocalDate.of(2010, Month.JULY, 7))
            .duration(201)
            .mpa(new Mpa(4, "NC-17"))
            .likes(new HashSet<>())
            .genres(new HashSet<>())
            .build();
    private final FilmStorage filmStorage;

    @Autowired
    public FilmDbStorageTest(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Test
    public void testFindFilmById() {
        filmStorage.add(film1);
        Film savedFilm = filmStorage.getById(1L);
        assertThat(savedFilm).isNotNull().usingRecursiveComparison().isEqualTo(film1);
    }

    @Test
    public void testFindAllFilms() {
        filmStorage.add(film1);
        filmStorage.add(film2);
        Collection<Film> films = filmStorage.getAllFilms().values();
        assertThat(films).hasSize(2);
    }

    @Test
    public void testAddFilm() {
        filmStorage.add(film1);
        Map<Long, Film> films = filmStorage.getAllFilms();
        Film savedFilm = films.get(1L);
        assertThat(savedFilm.getName()).isEqualTo(film1.getName());
        assertThat(savedFilm.getDescription()).isEqualTo(film1.getDescription());
        assertThat(savedFilm.getReleaseDate()).isEqualTo(film1.getReleaseDate());
        assertThat(savedFilm.getMpa()).isEqualTo(film1.getMpa());

    }

    @Test
    public void testUpdateFilm() {
        filmStorage.add(film1);
        Film updateFilm = Film.builder().name("Update")
                .id(1L)
                .description("Update")
                .releaseDate(LocalDate.of(2001, Month.NOVEMBER, 4))
                .duration(10)
                .mpa(new Mpa(5, "NC-17"))
                .likes(new HashSet<>())
                .genres(new HashSet<>())
                .build();
        filmStorage.update(updateFilm);
        Film savedFilm = filmStorage.getById(1L);
        assertThat(savedFilm).isNotNull().usingRecursiveComparison().isEqualTo(updateFilm);

    }

    @Test
    public void testDeleteFilm() {
        filmStorage.add(film1);
        filmStorage.delete(1L);
        assertThat(filmStorage.getAllFilms().isEmpty());
    }


}
