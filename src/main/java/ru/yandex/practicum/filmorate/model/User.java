package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {

    @NotNull
    private  int id;

    @NotNull
    @Email
    @NotBlank
    private final String email;

    @NotNull
    @NotBlank
    private final String login;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    private final LocalDate birthday;
}
