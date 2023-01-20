package ru.yandex.practicum.filmorate.storage.films;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.genres.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genres.GenreMapper;
import ru.yandex.practicum.filmorate.storage.genres.GenreStorage;
import ru.yandex.practicum.filmorate.storage.reitingsnpa.RatingMPAMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Component("filmsInDB")
public class FilmDbStorage implements FilmStorage {


    private final JdbcTemplate jdbcTemplate;
    private final RatingMPAMapper ratingMPAMapper;

    private final GenreMapper genreMapper;
    private final GenreDbStorage genreStorage;


    @Override
    public void delLike(long id, long userId) {
        String sqlQuery = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    @Override
    public Film update(Film film) {

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

        final String sqlQueryDelGenres = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQueryDelGenres, film.getId());
        String sqlQueryGenres = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
        if (film.getGenres() != null && film.getGenres().size() != 0) {
            List<Integer> genres = film.getGenres().stream()
                    .map(Genre::getId)
                    .distinct()
                    .collect(Collectors.toList());
            for (int i = 0; i < genres.size(); i++) {
                jdbcTemplate.update(sqlQueryGenres, film.getId(), genres.get(i));
            }
        }
        Film filmForReturn = getById(film.getId()).orElseThrow(() -> new NotFoundException("Фильм с Id = " + film.getId()
                + " не существует!"));
        log.info("Список жанров =  {}", filmForReturn.getGenres().size());
        return filmForReturn;
    }

    @Override
    public Film addLike(long id, long userId) {
        String sqlQuery = "INSERT INTO LIKES ( FILM_ID, USER_ID)" +
                "VALUES ( ?, ?)";
        Film film = getById(id).orElseThrow(() -> new NotFoundException("Фильм с Id = " + id + " не существует!"));
        jdbcTemplate.update(sqlQuery, id, userId);
        log.info("Возвращены данные о фильме {}", getById(id).get().toString());
        return film;
    }

    @Override
    public List<Film> getAll() {
        final String sqlQuery = "SELECT * " +
                "FROM FILM " +
                "JOIN MPA ON FILM.MPA_ID = MPA.ID";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm);
        log.info("Метод getAll(). Возвращен список фильмов : {}", films.size());
        return films;
    }


    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("realise_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(new RatingMpa(resultSet.getInt("mpa_id"), resultSet.getString("mpa.name")))

                // .genres(getGenresFromDB(resultSet.getInt("id")))
                .build();
    }


    private RatingMpa getMpaFromDb(long mpaId) {
        final String sqlQuery = "SELECT * " +
                " FROM mpa " +
                " WHERE id = ?";
        RatingMpa mpa = jdbcTemplate.queryForObject(sqlQuery, ratingMPAMapper, mpaId);
        log.info("Метод getMpaFromDb(), из базы данных извлечен mpa: {}", mpa.toString());
        return mpa;
    }


//////////////////////////////////////////////

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
        if (film.getGenres() != null && film.getGenres().size() != 0) {
            KeyHolder keyHolder2 = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                String sqlQueryGenres = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID)";
                List<Genre> genres = new ArrayList(film.getGenres());
                for (int i = 0; i < film.getGenres().size(); i++) {
                    sqlQueryGenres = sqlQueryGenres + "VALUES (" + film.getId() + ", " + genres.get(i).getId() + ")";
                }
                final PreparedStatement stmt2 = connection.prepareStatement(sqlQueryGenres);
                return stmt2;  // сгенерировано идеей
            }, keyHolder2);
            // устанавливаем фильму список жанров из б.д.
            // film.setGenres(getGenresFromDB(film.getId())); // использовалось для старого метода  getGenresFromDB

            List<Film> films = new ArrayList<>();
            films.add(film);

            Map<Long, List<Genre>> genresForFilms = genreStorage.getGenresFromDB(films);
            List<Genre> gnrs = genresForFilms.get(film.getId());

            LinkedHashSet<Genre> gnrForFilm = new LinkedHashSet<>();
            for (int i = 0; i < gnrs.size(); i++) {
                gnrForFilm.add(gnrs.get(i));

            }
            film.setGenres(gnrForFilm);
            // film.setGenres((LinkedHashSet<Genre>) genresForFilms.get(film.getId()));
        }
        // устанавливаем фильму mpa из базы данных
        film.setMpa(getMpaFromDb(film.getMpa().getId()));
        log.info("Метод add(), в базу данных добавлен фильм {}, c id {}", film.getName(), film.getId());
        return film;
    }


private void saveGenres(List<Genre> genres) {


}

    @Override
    public Optional<Film> getById(long id) {

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
            Film film = new Film(
                    filmRows.getLong("ID"),
                    filmRows.getString("NAME"),
                    filmRows.getString("DESCRIPTION"),
                    LocalDate.parse(filmRows.getString("REALISE_DATE")),
                    Long.parseLong(filmRows.getString("DURATION")),
                    getMpaFromDb(filmRows.getInt("MPA_ID")),
                    null
            );

            ///****

            List<Film> films = new ArrayList<>();
            films.add(film);
            Map<Long, List<Genre>> genresForFilms = genreStorage.getGenresFromDB(films);
            film.setGenres((LinkedHashSet<Genre>) genresForFilms.get(film.getId()));

            ///****


            log.info("______________Метод getById(), возвращен фильм с id {}, с названием {}", film.getId(), film.getName());
            return Optional.of(film);
        } else {
            log.info("Метод getById(). фильм с идентификаторо {} не найден.", id);
            throw new NotFoundException("Фильм не найден");
        }
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

        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm);
        genreStorage.getGenresFromDB(films);


        // использовалось для старого метода  getGenresFromDB
        //  for (int i = 0; i < films.size(); i++) {
        //    films.get(i).setGenres(getGenresFromDB(films.get(i).getId()));
        // }
        return films;
    }


}


//TODO пересмотреть этот метод
//  private LinkedHashSet<Genre> getGenresFromDB(long filmId) {
//    List<Genre> genres;
//    final String sqlQuery = "SELECT * " +
//            "FROM GENRE " +
//            "LEFT JOIN FILM_GENRE ON GENRE.ID = FILM_GENRE.GENRE_ID " +
//            "WHERE FILM_ID = ?";
//    genres = jdbcTemplate.query(sqlQuery, genreMapper, filmId);
//    log.info("Метод getGenresFromDB() Из базы данных извлечен список жанров: {}, Описание извлеченных жанров: {}", genres.size(), genres.toString());
//    LinkedHashSet<Genre> genresForReturn = new LinkedHashSet<>(genres);
//    return genresForReturn;
// }



/* //TODO старый метод - исключить цикл
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
        if (film.getGenres() != null && film.getGenres().size() != 0) {
            KeyHolder keyHolder2 = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                String sqlQueryGenres = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID)";
                List<Genre> genres = new ArrayList(film.getGenres());
                for (int i = 0; i < film.getGenres().size(); i++) {
                    sqlQueryGenres = sqlQueryGenres + "VALUES (" + film.getId() + ", " + genres.get(i).getId() + ")";
                }
                final PreparedStatement stmt2 = connection.prepareStatement(sqlQueryGenres);
                return stmt2;  // сгенерировано идеей
            }, keyHolder2);
            // устанавливаем фильму список жанров из б.д.
            // film.setGenres(getGenresFromDB(film.getId())); // использовалось для старого метода  getGenresFromDB

            List<Film> films = new ArrayList<>();
            films.add(film);

            Map<Long, List<Genre>> genresForFilms = genreStorage.getGenresFromDB(films);
            List<Genre> gnrs = genresForFilms.get(film.getId());

            LinkedHashSet<Genre> gnrForFilm = new LinkedHashSet<>();
            for (int i = 0; i < gnrs.size(); i++) {
                gnrForFilm.add(gnrs.get(i));

            }
            film.setGenres(gnrForFilm);
            // film.setGenres((LinkedHashSet<Genre>) genresForFilms.get(film.getId()));
        }
        // устанавливаем фильму mpa из базы данных
        film.setMpa(getMpaFromDb(film.getMpa().getId()));
        log.info("Метод add(), в базу данных добавлен фильм {}, c id {}", film.getName(), film.getId());
        return film;
    }
    */