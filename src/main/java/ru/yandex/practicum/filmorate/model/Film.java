package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;


import java.time.LocalDate;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Film {

    @Setter
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

    private RatingMpa mpa;

    private List<Genre> genres;

}