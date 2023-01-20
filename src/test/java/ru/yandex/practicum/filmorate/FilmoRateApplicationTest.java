package ru.yandex.practicum.filmorate.test;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.films.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.users.UserDbStorage;

import java.time.LocalDate;

import java.util.HashSet;
import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)

class FilmoRateApplicationTests {

    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    User userBefore = new User(1,
            "user1@email.ru",
            "User1login",
            "user1name",
            LocalDate.of(1990, 12, 15));

    Film filmBefore = new Film(1,
            "Фильм1",
            "Описание фильма 1",
            LocalDate.of(2022, 11, 5),
            120,
            new RatingMpa(1, "G"),
            null);

    LinkedHashSet<Genre> genres = new LinkedHashSet<>();
    Genre genre = new Genre(1, "Комедия");


    @Test
    public void testFindUserById() {
        genres.add(genre);
        filmBefore.setGenres(genres);
        userStorage.add(userBefore);
        User userAfter = userStorage.getById(1).orElseThrow(() -> new NotFoundException("Пользователь с Id = " + 1 + " не обнаружен"));

        assertEquals(userBefore.getId(), userAfter.getId());
        assertEquals(userBefore.getName(), userAfter.getName());
        assertEquals(userBefore.getBirthday(), userAfter.getBirthday());
        assertEquals(userBefore.getEmail(), userAfter.getEmail());
    }

    @Test
    public void testFindFilmById() {
        filmStorage.add(filmBefore);
        Film filmAfter = filmStorage.getById(1).orElseThrow(() -> new NotFoundException("Фильм с Id = " + 1 + " не обнаружен"));

        assertEquals(filmBefore.getId(), filmAfter.getId());
        assertEquals(filmBefore.getName(), filmAfter.getName());
        assertEquals(filmBefore.getDescription(), filmAfter.getDescription());
        assertEquals(filmBefore.getDuration(), filmAfter.getDuration());

    }
}

