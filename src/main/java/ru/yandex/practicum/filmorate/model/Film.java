package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Film {

    @Setter
    @NotNull
    private int id;

    @NotNull
    @NotBlank
    final private String name;

    @NotNull
    @NotBlank
    final private String description;

    @NotNull
    final private LocalDate releaseDate;

    @NotNull
    @Positive
    final private long duration;
}