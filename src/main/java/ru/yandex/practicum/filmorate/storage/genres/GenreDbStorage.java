package ru.yandex.practicum.filmorate.storage.genres;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
@Service
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
        final String sqlQuery = "SELECT * "
                + " FROM genre " +
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


    public Map<Long, List<Genre>> getGenresFromDB(List<Film> films) {

        Map<Long, List<Genre>> genresOfFilms = new HashMap<>();  // Map со списками жанров

        List<Long> idFIlms = new ArrayList<>();    // подготовка списка Id фильмов
        for (int i = 0; i < films.size(); i++) {
            idFIlms.add(films.get(i).getId());
        }

        for (int i = 0; i < films.size(); i++) {   // подготовка Map для заполнения
            genresOfFilms.put(films.get(i).getId(), new ArrayList<>());
        }

        String inSql = String.join(",", Collections.nCopies(idFIlms.size(), "?"));
        String allId = String.format("SELECT * FROM GENRE LEFT JOIN FILM_GENRE ON GENRE.ID = FILM_GENRE.GENRE_ID WHERE FILM_ID IN (%s)", inSql);
        SqlRowSet genresRows = jdbcTemplate.queryForRowSet(allId, idFIlms );

        while (genresRows.next()) {
            log.info("getGenresFromDB(). Найден жанр: id {} название {},  для фильма id {}",
                    genresRows.getLong("genre.id"),  // Long
                    genresRows.getString("genre.name"),
                    genresRows.getInt("film_genre.film_id"));

         // добавляем жанр в Map
            genresOfFilms.get(genresRows.getLong("film_genre.film_id")).add(new Genre(genresRows.getInt("id"), genresRows.getString("name")));
        }
        return genresOfFilms;
    }

    private Genre makeGenre(SqlRowSet genreRows) {
        Genre genre = new Genre(genreRows.getInt("id"),
                genreRows.getString("name"));
        return genre;
    }
}



