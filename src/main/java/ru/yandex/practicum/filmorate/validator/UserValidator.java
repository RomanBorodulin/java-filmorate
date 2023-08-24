package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class UserValidator {
    private final static LocalDate NOW = LocalDate.now();
    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public void validateAddUser(User user) throws ValidationException {
        if (user.getEmail().isBlank()) {
            log.warn("Введена пустая электронная почта");
            throw new ValidationException("Электронная почта не может быть пустой");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("Электронная почта {} не содержит символ \"@\"", user.getEmail());
            throw new ValidationException("Электронная почта должна содержать символ \"@\"");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Введен пустой логин или содержащий пробелы {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(NOW)) {
            log.warn("Введена дата рождения из будущего {}", user.getBirthday().format(DATE_FORMATTER));
            throw new ValidationException(String.format("Дата рождения не может быть позже %s",
                    NOW.format(DATE_FORMATTER)));
        }

    }
}
