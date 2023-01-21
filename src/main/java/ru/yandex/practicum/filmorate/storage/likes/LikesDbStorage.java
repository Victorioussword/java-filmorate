package ru.yandex.practicum.filmorate.storage.likes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.films.FilmStorage;


@Slf4j
@RequiredArgsConstructor
@Component
public class LikesDbStorage implements LikesStorage {

    private final  JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;

    @Override
    public Film addLike(long id, long userId) {
        String sqlQuery = "INSERT INTO LIKES ( FILM_ID, USER_ID)" +
                "VALUES ( ?, ?)";
        Film film = filmStorage.getById(id).orElseThrow(() -> new NotFoundException("Фильм с Id = " + id + " не существует!"));
        jdbcTemplate.update(sqlQuery, id, userId);
        log.info("Возвращены данные о фильме {}", filmStorage.getById(id).get().toString());
        return film;
    }

    @Override
    public void delLike(long id, long userId) {
        String sqlQuery = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
    }
}
