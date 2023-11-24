package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDbStorageTest {
    private final MpaStorage mpaStorage;

    @Test
    public void testFindMpaById() {
        Mpa mpa = new Mpa(1, "G");
        Mpa savedMpa = mpaStorage.getById(1);
        assertThat(savedMpa).isEqualTo(mpa);
    }

    @Test
    public void testFindAllMpa() {
        List<Mpa> mpa = mpaStorage.getAll();
        assertThat(mpa).hasSize(5);
    }
}
