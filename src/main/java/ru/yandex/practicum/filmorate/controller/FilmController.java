package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/films")
public class FilmController {

    private final static LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);
    private final static int MAX_SIZE_DESCRIPTION = 200;

    int id = 1;

    private Map<Integer, Film> films = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);


    @GetMapping
    public Collection<Film> getUsers() {
        log.info("GET /films. Возвращены данные о {} фильмах.", films.size());
        return films.values();
    }

    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        checkReleaseDate(film);
        checkName(film);
        checkSizeOfDescription(film);
        checkDuration(film);
        film.setId(id++);
        films.put(film.getId(), film);
        log.info("POST /films. Добавлены данные о фильме {}.", film);
        return film;
    }

    @PutMapping
    public Film putFilm(@Valid @RequestBody Film film) {
        checkReleaseDate(film);
        checkName(film);
        checkDuration(film);
        checkId(film);
        films.put(film.getId(), film);
        log.info("PUT /films. Обновлены данные о фильме {}.", film.toString());
        return film;
    }

    private void checkReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            log.info("Указаны не корректная дата выпуска {}.", film.getReleaseDate());
            throw new ValidationException("Кино не существовало до " + FIRST_FILM_DATE.toString());
        }
    }

    private void checkName(Film film) {
        if (film.getName() == null || film.getName().isEmpty() || film.getName().isBlank()) {
            log.info("Указано не корректное название.");
            throw new ValidationException("Название фильма не введено.");
        }
    }

    private void checkSizeOfDescription(Film film) {
        if (film.getDescription().length() > 200) {
            log.info("Указано не корректное описание.");
            throw new ValidationException("Слишком длинное описание, max =" + MAX_SIZE_DESCRIPTION + " символов.");
        }
    }

    private void checkDuration(Film film) {
        if (film.getDuration() < 0) {
            log.info("Указана не корректная длительность: {}.", film.getDuration());
            throw new ValidationException("Указана не корректная длительность");
        }
    }

    private void checkId(Film film) {
        if (!films.containsKey(film.getId())) {
            log.info("Обновление не существующего фильма: {}.", film.toString());
            throw new ValidationException("Не возможно обновить не существующий фильм");
        }
    }

}