package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTest {
    private final GenreStorage genreStorage;


    @Test
    public void testFindGenreById() {
        Genre genre = new Genre(1, "Комедия");
        Genre savedGenre = genreStorage.getById(1);
        savedGenre.setName(new String(savedGenre.getName().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
        assertThat(savedGenre).isEqualTo(genre);
    }

    @Test
    public void testFindAllGenres() {
        List<Genre> genres = genreStorage.getAll();
        assertThat(genres).hasSize(6);
    }

}
