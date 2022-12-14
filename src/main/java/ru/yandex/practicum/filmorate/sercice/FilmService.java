package ru.yandex.practicum.filmorate.sercice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {

   private final FilmStorage filmStorage;
   private final UserStorage userStorage;

  //private final FilmDbStorage filmStorage;
  //private  final UserDbStorage userStorage;

    public Map<Long, Film> getAll() {
        log.info("Возвращены данные о {} фильмах.", filmStorage.getAll().size());
        return filmStorage.getAll();
    }

    public Film add(Film film) {
        filmStorage.add(film);
        log.info("Добавлены данные о фильме {}.", film.toString());
        return film;
    }

    public Film getById(long id) {
        Film film = filmStorage.getById(id).orElseThrow(() -> new NotFoundException("Фильм с Id = " + id + " не существует!"));
        log.info("Возвращены данные о фильме {}", filmStorage.getById(id).get().toString());
        return film;
    }

    public Film update(Film film) {
        checkId(film.getId());
        filmStorage.update(film);
        log.info("Обновлены данные о фильме {}.", film.toString());
        return film;
    }

    public Film addLike(long id, long userId) {
        Film film = getById(id);
        checkUserId(userId);
        film.getLikes().add(userId);
        return filmStorage.update(film);
    }

    public Film delLike(long id, long userId) {
        checkUserId(userId);
        Film film = getById(id);
        film.getLikes().remove(userId);
        filmStorage.update(film);
        return film;
    }

    public Collection<Film> getPopular(int top) {
        return filmStorage.getAll().values().stream()
                .sorted((o1, o2) -> Integer.compare(o2.getLikes().size(), o1.getLikes().size()))
                .limit(top)
                .collect(Collectors.toList());
    }

    private void checkId(long id) {
        if (!filmStorage.getAll().containsKey(id)) {
            log.info("Фильм с id {} не найден", id);
            throw new NotFoundException("Фильм с id" + id + "не обнаружен");
        }
    }

    private void checkUserId(long id) {
        userStorage.getById(id).orElseThrow(() -> new NotFoundException("User с id" + id + "не обнаружен"));
    }

}
