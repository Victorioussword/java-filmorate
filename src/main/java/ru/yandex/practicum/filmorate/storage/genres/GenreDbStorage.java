package ru.yandex.practicum.filmorate.storage.genres;

import ch.qos.logback.core.joran.conditional.ElseAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;  // не уверен что это тот импорт

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

        //Long - ID фильма
        //List<Genre> - жанры фильма
        Map<Long, LinkedHashSet<Genre>> genresOfFilms = new HashMap<>(films.size());  // Map со списками жанров

        //Получаем MAP со всеми жанрами и их id
        Map<Integer, Genre> genres =
                getAll().stream().collect(Collectors.toMap(genre -> genre.getId(), Function.identity()));

        StringJoiner filmIds = new StringJoiner(",");
        for (Film film: films) {
            filmIds.add(Long.toString(film.getId()));
        }
        String sqlFilmsQuery = String.format(
                "SELECT film_id, genre_id FROM film_genre WHERE film_id IN (%s) ORDER BY film_id",
                filmIds
        );
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(sqlFilmsQuery);
        while (resultSet.next()) {
            long filmId = resultSet.getInt("film_id");
            int filmGenreId = resultSet.getInt("genre_id");
            Genre filmGenre = genres.get(filmGenreId);

            log.info("getGenresFromDB(). Найден жанр: id {} название {},  для фильма id {}",
                    filmGenreId,
                    filmGenre.getName(),
                    filmId
            );

            if (!genresOfFilms.containsKey(filmId)) {

                genresOfFilms.put(filmId, new LinkedHashSet<>());
            }

            genresOfFilms.get(filmId).add(filmGenre);

        }

        for (Film film: films) {
            film.setGenres(genresOfFilms.get(film.getId()));
        }

    }


    private Genre makeGenre(SqlRowSet genreRows) {
        Genre genre = new Genre(genreRows.getInt("id"),
                genreRows.getString("name"));
        return genre;
    }


    public Map<Long, List<Genre>> getGenresFromDB2(List<Film> films) {


        Map<Long, List<Genre>> genresOfFilms = new HashMap<>(films.size()); // Map со списками жанров КЛюч - ID фильма - Для возврата!!!

        Map<Integer, Genre> genres = getAll().stream().
                collect(Collectors.toMap(genre -> genre.getId(), identity()));  //Получаем MAP со всеми жанрами и их id

        String allId = String.format("SELECT film_id, genre_id FROM film_genre ORDER BY film_id ");  // Запрашиваем фильмы и жанры
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(allId);

        while (resultSet.next()) {    // Собираем Жанр
            long filmId = resultSet.getInt("film_id");
            int filmGenreId = resultSet.getInt("genre_id");
            Genre filmGenre = genres.get(filmGenreId);
            log.info("getGenresFromDB(). Найден жанр: id {} название {},  для фильма id {}",
                    filmGenreId,
                    filmGenre.getName(),
                    filmId
            );
            if (!genresOfFilms.containsKey(filmId)) {
                genresOfFilms.put(filmId, new ArrayList<>());
            }
            genresOfFilms.get(filmId).add(filmGenre);
        }
        return genresOfFilms;
    }

}



