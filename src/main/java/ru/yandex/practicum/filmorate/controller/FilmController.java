package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.sercice.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/films")
public class FilmController {

    private final static LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);

    private final FilmService filmService;

    @GetMapping
    public List<Film> get() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable int id) {
        return filmService.getById(id);
    }

    @PostMapping
    public Film post(@Valid @RequestBody Film film) {
        checkReleaseDate(film);
        return filmService.add(film);
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        log.info("Controller - Фильм для обновления mpa = {}.", film.getMpa().getId());
        checkReleaseDate(film);
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable long id, @PathVariable long userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film delLike(@PathVariable long id, @PathVariable long userId) {
        return filmService.delLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10", required = false) @Positive Integer count) {
        return filmService.getPopular(count);
    }

    private void checkReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            log.info("Указаны не корректная дата выпуска {}.", film.getReleaseDate());
            throw new ValidationException("Кино не существовало до " + FIRST_FILM_DATE.toString());
        }
    }
}