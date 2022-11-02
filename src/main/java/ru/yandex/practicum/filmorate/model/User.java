package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;



import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {

    @PastOrPresent
    @NotNull
    private final LocalDate birthday;


    private  long id;


    @Email
    @NotNull
    private final String email;


    @NotBlank
    private final String login;

    private String name;
}
