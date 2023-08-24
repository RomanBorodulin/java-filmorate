package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class Film {
    private int id;
    @NonNull
    private String name;
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @NonNull
    private Integer duration;

}
