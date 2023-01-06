package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor

public class User {
    private long id;

    @Email
    @NotNull
    private final String email;

    @NotBlank
    private final String login;  //не содержит пробелов


    private String name;  // Если name пустое - используется Login

    @PastOrPresent
    @NotNull
    private final LocalDate birthday;

  }
