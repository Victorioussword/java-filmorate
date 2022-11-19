package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
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
