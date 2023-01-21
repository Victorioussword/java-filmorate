package ru.yandex.practicum.filmorate.storage.films;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> getAll();

    Film add(Film film);

    Film update(Film film);

    Optional<Film> getById(long id);

    List<Film> getPopular(int top);

}
