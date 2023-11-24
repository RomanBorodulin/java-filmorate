package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class User {
    private long id;
    @NotNull
    @Email
    private String email;
    @NotNull
    private String login;
    private String name;
    @NotNull
    private LocalDate birthday;
    @JsonIgnore
    private Set<Long> friends;

    public void addFriend(long id) {
        friends.add(id);
    }

    public void removeFriendById(long id) {
        friends.remove(id);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("email", email);
        values.put("login", login);
        values.put("name", name);
        values.put("birthday", birthday);
        return values;
    }
}
