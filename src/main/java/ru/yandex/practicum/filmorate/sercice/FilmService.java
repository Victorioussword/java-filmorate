package ru.yandex.practicum.filmorate.sercice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.films.FilmStorage;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    public Film add(Film film) {
        filmStorage.add(film);
        System.out.println(film.getGenres());
        log.info("Добавлены данные о фильме {}.", film.toString());
        return film;
    }


    public List<Film> getAll() {
        log.info("Возвращены данные о {} фильмах.", filmStorage.getAll().size());
        return filmStorage.getAll();
    }

    public Film getById(long id) {
        Film film = filmStorage.getById(id).orElseThrow(() -> new NotFoundException("Фильм с Id = " + id + " не существует!"));
        log.info("Возвращены данные о фильме {}", filmStorage.getById(id).get().toString());
        return film;
    }

    public Film update(Film film) {

        checkId(film.getId());

        log.info("Фильм пришедший на обновление id= {}, name = {}, mpa = {}.",
                film.getId(),
                film.getName(),
                film.getMpa().getId());

        Film filmForReturn = filmStorage.update(film);

        log.info("Обновлены данные о фильме id= {}, name = {}, mpa = {}.",
                filmForReturn.getId(),
                filmForReturn.getName(),
                filmForReturn.getMpa().getId());

        return filmForReturn;
    }

    public Film addLike(long id, long userId) {
        Film film = getById(id);  // проверка наличие фильма в базе
        checkUserId(userId);  // проверка наличия пользователя в базе
        filmStorage.addLike(id, userId);

        return film;
    }

    public Film delLike(long id, long userId) {
        checkUserId(userId);
        Film film = getById(id);
        filmStorage.delLike(id, userId);
        return film;
    }

    public List<Film> getPopular(int top) {
        return filmStorage.getPopular(top);
    }

    private Map<Long, Film> checkId(long id) {
        List<Film> films = filmStorage.getAll();
        Map<Long, Film> filmsForCheck = new HashMap<>();

        for (int i = 0; i < films.size(); i++) {
            filmsForCheck.put(films.get(i).getId(), films.get(i));
        }
        if (!filmsForCheck.containsKey(id)) {
            log.info("Фильм с id {} не найден", id);
            throw new NotFoundException("Фильм с id = " + id + " не обнаружен");
        }
        return filmsForCheck;
    }

    private void checkUserId(long id) {
        userStorage.getById(id).orElseThrow(() -> new NotFoundException("User с id " + id + " не обнаружен"));
    }

    public List<Genre> getGenres() {
        return null;
    }
}
