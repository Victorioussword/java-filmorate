package ru.yandex.practicum.filmorate.storage.genres;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;



@Slf4j
@RequiredArgsConstructor
@Component
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreMapper genreMapper;


    public List<Genre> getAll() {
        final String sqlQuery = "SELECT *"
                + " FROM genre";

        List<Genre> genres = jdbcTemplate.query(sqlQuery, genreMapper);
        return genres;
    }

    public Optional<Genre> getById(int id) {
        final String sqlQuery = "SELECT * " +
                " FROM genre " +
                "WHERE id = ?";

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


    public void getGenresFromDB(List<Film> films) {

        Map<Long, Film> filmMap = films.stream().collect(Collectors.toMap(Film::getId, film -> film));

        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));

        final String sqlQuery = "select * from GENRE g, FILM_GENRE fg where fg.GENRE_ID = g.ID AND fg.FILM_ID in (" + inSql + ")";

        jdbcTemplate.query(sqlQuery, (rs) -> {
            filmMap.get(rs.getLong("FILM_ID")).addGenre(makeGenre(rs));

        }, filmMap.keySet().toArray());

    }


    private Genre makeGenre(SqlRowSet genreRows) {
        Genre genre = new Genre(genreRows.getInt("id"),
                genreRows.getString("name"));
        return genre;
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre(rs.getInt("id"),
                rs.getString("name"));
        return genre;
    }
}



