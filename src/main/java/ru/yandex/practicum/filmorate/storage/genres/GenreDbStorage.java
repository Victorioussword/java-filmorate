package ru.yandex.practicum.filmorate.storage.genres;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.users.UserMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Optional;

@Slf4j

@Component
@Primary
@Repository

@AllArgsConstructor
public class GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public List<Genre> getAll() {
        final String sqlQuery = "SELECT *"
                + " FROM GENRE";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, new GenreMapper());
        return genres;
    }


    private Genre makeGenre(SqlRowSet genreRows) {
        Genre genre = new Genre(genreRows.getInt("ID"),
                genreRows.getString("NAME"));
        return genre;
    }


    public Optional<Genre> getById(int id) {

        final String sqlQuery = "SELECT * "
                + " FROM GENRE " +
                "WHERE ID = ?";

        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQuery, new Object[]{id});

        if (genreRows.next()) {
            Genre genre = makeGenre(genreRows);
            log.info("Найден жанр: {} {}", genre.getId(), genre.getName());
            return Optional.of(genre);
        } else {
            log.info("Жанр с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }


}



