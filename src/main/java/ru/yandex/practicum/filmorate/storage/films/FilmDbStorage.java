package ru.yandex.practicum.filmorate.storage.films;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.genres.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.reitingsnpa.RatingMPAMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


@Slf4j
@RequiredArgsConstructor
@Component("filmsInDB")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final RatingMPAMapper ratingMPAMapper;
    private final GenreDbStorage genreStorage;

    @Override
    public Film add(Film film) {
        final String sqlQuery = "INSERT INTO FILM ( NAME, DESCRIPTION, REALISE_DATE, DURATION) " +
                "VALUES ( ?, ?, ?, ? )";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            final PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, new String[]{"id"});
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setLong(4, film.getDuration());
            return preparedStatement;
        }, keyHolder);
        // устанавливаем film новый id, полученный после вставки в базу данных
        film.setId(keyHolder.getKey().longValue());
        final String sqlQueryMpa = "UPDATE FILM SET MPA_ID = ? WHERE ID = ?";
        jdbcTemplate.update(sqlQueryMpa, film.getMpa().getId(), film.getId());
        // заносим даннные о жанрах в б.д.
        saveGenres(film);
        Film film2 = getById(film.getId()).orElseThrow(() -> new NotFoundException("Фильм с Id = " + film.getId() + " не существует!"));
        log.info("Метод add(), в базу данных добавлен фильм {}, c id {}", film.getName(), film.getId());
        return film2;
    }


    @Override
    public Film update(Film film) {

        final String sqlQueryDelGenres = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQueryDelGenres, film.getId());
        final String sqlQueryFilm = "UPDATE FILM SET " +
                "NAME = ?, " +
                "DESCRIPTION = ?, " +
                "REALISE_DATE = ?, " +
                "DURATION = ?, " +
                "MPA_ID = ? " +
                "WHERE ID = ?";
        jdbcTemplate.update(sqlQueryFilm,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        saveGenres(film);
        Film filmForReturn = getById(film.getId()).orElseThrow(() -> new NotFoundException("Фильм с Id = " + film.getId()
                + " не существует!"));
        return filmForReturn;
      //  return film;
    }


    @Override
    public Optional<Film> getById(long id) {
        Film filmForReturn;
        final String sqlQueryFILM_and_MPA = "SELECT " +
                "FILM.ID, " +                 // 1
                "FILM.NAME, " +               // 2
                "FILM.DESCRIPTION, " +        // 3
                "FILM.REALISE_DATE, " +       // 4
                "FILM.DURATION, " +           // 5
                "FILM.MPA_ID , " +            // 6
                "MPA.ID, " +                  // 7
                "MPA.NAME " +                 // 8
                "FROM FILM " +
                "LEFT OUTER JOIN MPA ON FILM.MPA_ID = MPA.ID " +
                "WHERE FILM.ID = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQueryFILM_and_MPA, new Object[]{id});

        if (filmRows.next()) {
            filmForReturn = makeFilm(filmRows);
            log.info("Метод getById(), возвращен фильм с id {}, с названием {}", filmForReturn.getId(), filmForReturn.getName());
          //  log.info("______________!!!!!!!!!!!!!!!ПРОВЕРКА!!!!!! {}", filmForReturn.getGenres().isEmpty());
            return Optional.of(filmForReturn);
        } else {
            log.info("Метод getById(). фильм с идентификаторо {} не найден.", id);
            throw new NotFoundException("Фильм не найден");
        }
    }


    @Override
    public List<Film> getAll() {
        final String sqlQuery = "SELECT * " +
                "FROM FILM " +
                "JOIN MPA ON FILM.MPA_ID = MPA.ID";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::buildFilm);  // Все фильмы без жанров
        log.info("Метод getAll(). Возвращен список фильмов : {}", films.size());
        genreStorage.getGenresFromDB(films);
        return films;
    }


    @Override
    public List<Film> getPopular(int top) {
        final String sqlQuery = "SELECT f.id, " +
                "f.name, " +
                "f.description, " +
                "f.realise_date, " +
                "f.duration, " +
                "f.mpa_id, " +
                "m.name, " +
                "COUNT (user_id) " +
                "FROM film  AS f " +
                "LEFT JOIN likes AS l ON f.id = l.film_id " +
                "LEFT JOIN mpa as m ON f.mpa_id = m.id " +   // эта строка добавлена 06.01.23
                "GROUP BY (f.id) " +
                "ORDER BY COUNT (user_id) DESC " +
                "LIMIT " + top;

        List<Film> films = jdbcTemplate.query(sqlQuery, this::buildFilm);
        return films;
    }


    private void saveGenres(Film film) {

        if (film.getGenres() != null && film.getGenres().size() != 0) {
            List<Genre> genres = new ArrayList<>(film.getGenres());
            String sqlQueryGenres = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(sqlQueryGenres, genres, 100,
                    (PreparedStatement ps, Genre genre) -> {
                        ps.setLong(1, film.getId());
                        ps.setLong(2, genre.getId());
                    });
        }
    }

    private Film buildFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("realise_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(new RatingMpa(resultSet.getInt("mpa_id"), resultSet.getString("mpa.name")))
                .genres(new LinkedHashSet<>(10))
                .build();
    }

    private Film makeFilm(SqlRowSet filmRows) {
        Film film = new Film(
                filmRows.getLong("ID"),
                filmRows.getString("NAME"),
                filmRows.getString("DESCRIPTION"),
                LocalDate.parse(filmRows.getString("REALISE_DATE")),
                Long.parseLong(filmRows.getString("DURATION")),
                new RatingMpa(filmRows.getInt(7), filmRows.getString(8)),
                new LinkedHashSet<>(10)
        );
        List<Film> films = new ArrayList<>();
        films.add(film);

        genreStorage.getGenresFromDB(films);

        return films.get(0);
    }
}
