package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;


import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {

    @PastOrPresent
    @NotNull
    private final LocalDate birthday;

    private long id;

    @Email
    @NotNull
    private final String email;

    @NotBlank
    private final String login;

    private String name;

    private final Set<Long> friends = new HashSet<>();
}
