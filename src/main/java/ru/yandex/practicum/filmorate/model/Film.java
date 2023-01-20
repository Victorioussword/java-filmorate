package ru.yandex.practicum.filmorate.model;

import lombok.*;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Film {

  // удалена аннотация
    private long id;

    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 200)
     private String description;

    @NotNull
     private LocalDate releaseDate;

    @Positive
   private long duration;
    @NotNull // добавлена аннотация
    private RatingMpa mpa;

    private LinkedHashSet<Genre> genres;  // Теперь используется LinkedHashSet

}