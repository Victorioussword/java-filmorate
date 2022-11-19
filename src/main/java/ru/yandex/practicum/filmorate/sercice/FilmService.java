package ru.yandex.practicum.filmorate.sercice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmStorage inMemoryFilmStorage;
    private final UserStorage inMemoryUserStorage;

    public Map<Long, Film> getAll() {
        log.info("Возвращены данные о {} фильмах.", inMemoryFilmStorage.getAll().size());
        return inMemoryFilmStorage.getAll();
    }

    public Film add(Film film) {

        inMemoryFilmStorage.add(film);
        log.info("Добавлены данные о фильме {}.", film.toString());
        return film;
    }

    public Film getById(long id) {

        Optional<Film> filmOpt = inMemoryFilmStorage.getById(id);
        Film film = filmOpt.orElseThrow(() -> new NotFoundException("Фильм с Id = " + id + " не существует!"));
        log.info("Возвращены данные о фильме {}", filmOpt.get().toString());
        return film;
    }

    public Film update(Film film) {

        inMemoryFilmStorage.update(film);
        log.info("Обновлены данные о фильме {}.", film.toString());
        return film;
    }

    public Film addLike(long id, long userId) {

        Optional<Film> filmOptId = inMemoryFilmStorage.getById(id);
        Film filmId = filmOptId.orElseThrow(() -> new NotFoundException("Указан не существующий Id фильма или не существующий Id пользователя"));

        Optional<Film> filmOptUserId = inMemoryFilmStorage.getById(userId);
        Film filmUserId = filmOptUserId.orElseThrow(() -> new NotFoundException("Указан не существующий Id фильма или не существующий Id пользователя"));

        filmId.getLikes().add(filmUserId.getId());
        return inMemoryFilmStorage.update(filmId);
    }

    public Film delLike(long id, long userId) {

        Optional<User> userOpt = inMemoryUserStorage.getById(userId);
        userOpt.orElseThrow(() -> new NotFoundException("User с id" + id + "не обнаружен"));

        Optional<Film> filmOpt = inMemoryFilmStorage.getById(id);
        Film film = filmOpt.orElseThrow(() -> new NotFoundException("Фильм с id" + id + "не обнаружен"));
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
}
