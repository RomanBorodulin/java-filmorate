package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class Film {
    private long id;
    @NotNull
    private String name;
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    private Integer duration;
    @JsonIgnore
    private Set<Long> likes;
    private Mpa mpa;
    private Set<Genre> genres;

    public void addLike(long id) {
        likes.add(id);
    }

    public void removeLikeById(long id) {
        likes.remove(id);
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("rating_id", mpa.getId());
        return values;
    }
}
