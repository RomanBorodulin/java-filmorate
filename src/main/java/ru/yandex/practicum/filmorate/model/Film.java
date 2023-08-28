package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class Film {
    private int id;
    @NotNull
    private String name;
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    private Integer duration;

}
