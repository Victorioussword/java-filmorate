package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Data
@AllArgsConstructor
public class Film {

    @Setter
    private long id;

    @NotBlank
    final private String name;

    @NotBlank
    @Size(max = 200)
    final private String description;

    @NotNull
    final private LocalDate releaseDate;

    @Positive
    final private long duration;

    private final Set<Long> likes = new HashSet<>();

}