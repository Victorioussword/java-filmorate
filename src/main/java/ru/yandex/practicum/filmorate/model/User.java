package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;


import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor

public class User {
    private long id;

    @Email
    @NotEmpty
    private final String email;

    @NotBlank
    private final String login;  // не состоит из пробелов

    private String name;  // Если name пустое - используется Login

    @PastOrPresent
    @NotNull
    private final LocalDate birthday;

  }
