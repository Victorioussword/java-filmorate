package ru.yandex.practicum.filmorate.storage.likes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class LikesDbStorage implements LikesStorage {

    private final  JdbcTemplate jdbcTemplate;


    @Override
    public void addLike(long id, long userId) {
        String sqlQuery = "INSERT INTO LIKES ( FILM_ID, USER_ID)" +
                "VALUES ( ?, ?)";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    @Override
    public void delLike(long id, long userId) {
        String sqlQuery = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
    }
}
