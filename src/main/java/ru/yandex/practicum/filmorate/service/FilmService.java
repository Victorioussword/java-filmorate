package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import ru.yandex.practicum.filmorate.storage.films.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genres.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genres.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;


import java.util.ArrayList;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikesStorage likesStorage;
    private final GenreStorage genreStorage;

    public Film add(Film film) {
        filmStorage.add(film);
        log.info("Добавлены данные о фильме {}.", film.toString());
        return film;
    }

    public List<Film> getAll() {
        List<Film> filmsForReturn = filmStorage.getAll();
        log.info("Возвращены данные о {} фильмах.", filmsForReturn.size());
        genreStorage.getGenresFromDB(filmsForReturn);
        return filmsForReturn;
    }

    public Film getById(long id) {
        Film film = filmStorage.getById(id);
        log.info("Возвращены данные о фильме {}", film.toString());

        List<Film> films = new ArrayList<>();
        films.add(film);
        genreStorage.getGenresFromDB(List.of(film));
        return film;
    }

    public Film update(Film film) {
        filmStorage.getById(film.getId());
        log.info("Фильм пришедший на обновление id= {}, name = {}, mpa = {}.",
                film.getId(),
                film.getName(),
                film.getMpa().getId());
        Film filmForReturn = filmStorage.update(film);
        log.info("Обновлены данные о фильме id= {}, name = {}, mpa = {}.",
                filmForReturn.getId(),
                filmForReturn.getName(),
                filmForReturn.getMpa().getId());
        return film;
    }


    public List<Film> getPopular(int top) {
        return filmStorage.getPopular(top);
    }


    private void checkUserId(long id) {
        userStorage.getById(id);
    }


    public Film addLike(long id, long userId) {
        Film film = getById(id);
        checkUserId(userId);
        likesStorage.addLike(id, userId);
        return film;
    }

    public Film delLike(long id, long userId) {
        checkUserId(userId);
        Film film = getById(id);
        likesStorage.delLike(id, userId);
        return film;
    }
}
