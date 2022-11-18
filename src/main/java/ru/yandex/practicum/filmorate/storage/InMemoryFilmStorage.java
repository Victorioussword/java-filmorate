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
        Optional<Film> filmOpt = Optional.of(films.get(id));
        return filmOpt;
    }
}

/**
 * (Предложение) Хорошо было бы использовать в методах получения по идентификатору тип Optional
 * https://habr.com/ru/post/346782/ Гарантирую, ты не пожалеешь,
 * а заодно в будущем тебе будет проще, так как он активно используется при работе со Spring Data
 * Если будешь использовать тип Optional, то здесь как раз будет проверка,
 * можно будет использовать простую лямбду .orElseThrow(() -> new NotFoundExcetion(...));
 * То есть, если объект не будет найден, будет выброшено исключение, а если он есть,
 * мы просто его получим, как будто написали .get()
 * Это для метода получения по идентификатору, к которому, кстати, можно потом обращаться,
 * чтобы не дублировать строку с выбросом исключения)
 */


/**Если будешь использовать тип Optional, то здесь как раз будет проверка,
 * можно будет использовать простую лямбду .orElseThrow(() -> new NotFoundExcetion(...));
 * То есть, если объект не будет найден, будет выброшено исключение, а если он есть,
 * мы просто его получим, как будто написали .get()
 Это для метода получения по идентификатору, к которому, кстати, можно потом обращаться,
 чтобы не дублировать строку с выбросом исключения)*/