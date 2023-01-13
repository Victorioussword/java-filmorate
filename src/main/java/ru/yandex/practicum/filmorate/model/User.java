package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;


import java.time.LocalDate;

@Data
@AllArgsConstructor

public class User {
    private long id;

    @Email
    @NotNull
    private final String email;

    @NotBlank
    private final String login;  // не состоит из пробелов

    private String name;  // Если name пустое - используется Login

    @PastOrPresent
    @NotNull
    private final LocalDate birthday;

  }
