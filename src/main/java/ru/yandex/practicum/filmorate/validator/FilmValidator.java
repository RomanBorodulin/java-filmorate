package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class FilmValidator {
    private final static LocalDate MOVIE_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER, 28);
    private final static int MAX_LENGTH_DESCRIPTION = 200;
    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public void validateAddFilm(Film film) throws ValidationException {
        if (film.getName().isBlank()) {
            log.warn("Введено пустое название фильма");
            throw new ValidationException("Отсутствует название фильма");
        }
        if (film.getDescription() != null && film.getDescription().length() > MAX_LENGTH_DESCRIPTION) {
            log.warn("Длина описания {} превышает максимальную длину = {}",
                    film.getDescription().length(), MAX_LENGTH_DESCRIPTION);
            throw new ValidationException("Превышена максимальная длина описания");
        }
        if (film.getReleaseDate().isBefore(MOVIE_BIRTHDAY)) {
            log.warn("Дата релиза {} - раньше {}", film.getReleaseDate().format(DATE_FORMATTER),
                    MOVIE_BIRTHDAY.format(DATE_FORMATTER));
            throw new ValidationException(String.format("Дата релиза указана раньше %s",
                    MOVIE_BIRTHDAY.format(DATE_FORMATTER)));
        }
        if (film.getDuration() <= 0) {
            log.warn("Продолжительность фильма {} не является положительной", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }

    }
}
