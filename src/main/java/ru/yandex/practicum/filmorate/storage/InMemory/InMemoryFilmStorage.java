package ru.yandex.practicum.filmorate.storage.InMemory;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

// TODO удалить всю реализацию в памяти

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private long id = 1;

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Map<Long, Film> getAll() {
        Map<Long, Film> forReturn = new HashMap<>();
        forReturn.putAll(films);
        return forReturn;
    }

    @Override
    public Film add(Film film) {
        film.setId(id++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> getById(long id) {
        if (!films.containsKey(id)) {
            return Optional.empty();
        } else {
            return Optional.of(films.get(id));
        }
    }
}
