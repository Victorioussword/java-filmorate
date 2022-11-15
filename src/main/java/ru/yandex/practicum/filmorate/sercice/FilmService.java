package ru.yandex.practicum.filmorate.sercice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {

    private final static LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);

    private int id = 1;
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;


    public Collection<Film> getAll() {
        log.info("Возвращены данные о {} фильмах.", inMemoryFilmStorage.getAll().size());
        return inMemoryFilmStorage.getAll().values();
    }

    public Film add(@Valid Film film) {
        checkReleaseDate(film);
        film.setId(id++);
        inMemoryFilmStorage.add(film);
        log.info("Добавлены данные о фильме {}.", film.toString());
        return film;
    }

    public Film getById(long id) {
        if (!inMemoryFilmStorage.getAll().containsKey(id)) {
            throw new NotFoundException("Фильм с Id = " + id + " не существует!");
        }
        Film film = inMemoryFilmStorage.getById(id);
        log.info("Возвращены данные о фильме {}", film.toString());
        return film;
    }

    public Film update(@Valid Film film) {
        checkReleaseDate(film);
        checkId(film.getId());
        inMemoryFilmStorage.add(film);   // проверить какой фильм добавляется и возвращается
        log.info("Обновлены данные о фильме {}.", film.toString());
        return film;
    }

    public Film addLike(long id, long userId) {
        if (!inMemoryFilmStorage.getAll().containsKey(id) || !inMemoryUserStorage.getAll().containsKey(userId)) {
            throw new NotFoundException("Указан не существующий Id фильма или не существующий Id пользователя");
        }
        Film film = inMemoryFilmStorage.getById(id);
        film.getLikes().add(userId);
        inMemoryFilmStorage.update(film);
        return film;
    }

    public Film delLike(long id, long userId) {
        checkId(id);
        if (!inMemoryUserStorage.getAll().containsKey(userId)) {
            log.info("Пользователь с  {} не существует", userId);
            throw new NotFoundException("Потльзователь с id" + userId + "не обнаружен");
        }
        Film film = inMemoryFilmStorage.getById(id);
        for (long like : film.getLikes()) {
            if (like == userId) {
                film.getLikes().remove(userId);
            }
        }
        inMemoryFilmStorage.update(film);
        return film;
    }

    public Collection<Film> getPopular(int top) {
        return inMemoryFilmStorage.getAll().values().stream()
                .sorted((o1, o2) -> Integer.compare(o2.getLikes().size(), o1.getLikes().size()))
                .limit(top)
                .collect(Collectors.toList());
    }

    private void checkReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            log.info("Указаны не корректная дата выпуска {}.", film.getReleaseDate());
            throw new ValidationException("Кино не существовало до " + FIRST_FILM_DATE.toString());
        }
    }

    private void checkId(long id) {
        if (!inMemoryFilmStorage.getAll().containsKey(id)) {
            log.info("Фильм с id {} не найден", id);
            throw new NotFoundException("Фильм с id" + id + "не обнаружен");
        }
    }
}
