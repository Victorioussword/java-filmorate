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
import ru.yandex.practicum.filmorate.storage.genres.GenreMapper;
import ru.yandex.practicum.filmorate.storage.reitingsnpa.RatingMPAMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Component("filmsInDB")
@Primary
@Repository
public class FilmDbStorage implements FilmStorage {


    private final JdbcTemplate jdbcTemplate;


    @Override
    public Film add(Film film) {
        final String sqlQuery = "INSERT INTO FILM ( NAME, DESCRIPTION, REALISE_DATE, DURATION) " +
                "VALUES ( ?, ?, ?, ? )";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            final PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, new String[]{"ID"});
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setLong(4, film.getDuration());
            return preparedStatement;
        }, keyHolder);

        // устанавливаем film новый id, полученный после вставки в базу данных
        film.setId(keyHolder.getKey().longValue());

        final String sqlQueryMpa = "UPDATE FILM SET mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sqlQueryMpa, film.getMpa().getId(), film.getId());

        // заносим даннные о жанрах в б.д.
        if (film.getGenres() != null && film.getGenres().size() != 0) {
            KeyHolder keyHolder2 = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                String sqlQueryGenres = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID)";

                for (int i = 0; i < film.getGenres().size(); i++) {
                    sqlQueryGenres = sqlQueryGenres + "VALUES (" + film.getId() + ", " + film.getGenres().get(i).getId() + ")";
                }
                final PreparedStatement stmt2 = connection.prepareStatement(sqlQueryGenres);
                return stmt2;  // сгенерировано идеей
            }, keyHolder2);

            // устанавливаем фильму список жанров из б.д.
            film.setGenres(getGenresFromDB(film.getId()));
        }
        // устанавливаем фильму mpa из базы данных
        film.setMpa(getMpaFromDb(film.getMpa().getId()));
        log.info("Метод add(), в базу данных добавлен фильм {}, c id {}", film.getName(), film.getId());
        return film;
    }


    private RatingMpa getMpaFromDb(long mpaId) {
        final String sqlQuery = "SELECT * " +
                " FROM mPA " +
                " WHERE ID = ?";

        RatingMpa mpa = jdbcTemplate.queryForObject(sqlQuery, new RatingMPAMapper(), mpaId);
        log.info("Метод getMpaFromDb(), из базы данных извлечен mpa: {}", mpa.toString());
        return mpa;
    }


    private List<Genre> getGenresFromDB(long filmId) {
        List<Genre> genres;
        final String sqlQuery = "SELECT * " +
                "FROM GENRE " +
                "LEFT JOIN FILM_GENRE ON GENRE.ID = FILM_GENRE.GENRE_ID " +
                "WHERE FILM_ID = ?";
        genres = jdbcTemplate.query(sqlQuery, new GenreMapper(), filmId);
        log.info("Метод getGenresFromDB() Из базы данных извлечен список жанров: {}, Опиасание извлеченных жанров: {}", genres.size(), genres.toString());
        return genres;
    }


    @Override
    public Optional<Film> getById(long id) {

        final String sqlQueryFILM_and_MPA = "SELECT " +
                "film.id, " +                // 1
                "film.name, " +              // 2
                "film.description, " +        // 3
                "film.realise_date, " +       // 4
                "film.duration, " +           // 5
                "film.mpa_id , " +            // 6
                "mpa.id, " +                  // 7
                "mpa.name " +                 // 8
                " FROM film " +
                "LEFT OUTER JOIN mpa ON film.mpa_id = mpa.id " +
                " WHERE film.id = ?";

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQueryFILM_and_MPA, new Object[]{id});

        if (filmRows.next()) {
            Film film = new Film(
                    filmRows.getLong("id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    LocalDate.parse(filmRows.getString("realise_date")),
                    Long.parseLong(filmRows.getString("duration")),
                    getMpaFromDb(filmRows.getInt("mpa_id")),
                    //   new RatingMpa(filmRows.getInt(5), filmRows.getString(6)), // эта строка не заработала
                    getGenresFromDB(id)
            );

            log.info("Метод getById(), возвращен фильм с id {}, с названием {}", film.getId(), film.getName());
            return Optional.of(film);
        } else {
            log.info("Метод getById(). фильм с идентификаторо {} не найден.", id);
            throw new NotFoundException("Фильм не найден");
        }
    }




    @Override
    public void delLike(long id, long userId) {
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
    }




    private Film makeFilmForGetAll(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("realise_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(new RatingMpa(resultSet.getInt("mpa_id"), resultSet.getString("mpa.name")))
                .genres(getGenresFromDB2(resultSet.getInt("id")))
                .build();
    }


    private List<Genre> getGenresFromDB2(long filmId) {
        List<Genre> genres;
        final String sqlQuery = "SELECT * " +
                "FROM FILM_GENRE " +
                "LEFT JOIN GENRE ON  FILM_GENRE.GENRE_ID = GENRE.ID " +
                "WHERE FILM_ID = ?";
        genres = jdbcTemplate.query(sqlQuery, this::makeGenre, filmId);
        log.info("Метод getGenresFromDB() Из базы данных извлечен список жанров: {}, Опиасани извлеченных жанров: {}",
                genres.size(), genres.toString());
        return genres;
    }


    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        Genre genre = new Genre(rs.getInt("id"), rs.getString("name"));
        log.info("Метод makeGenre(), ID =  {},  name = {}", genre.getId(), genre.getName());
        return genre;
    }


    @Override
    public Film update(Film film) {

        final String sqlQueryFilm = "UPDATE film SET " +
                "name = ?, " +
                "description = ?, " +
                "realise_date = ?, " +
                "duration = ?, " +
                "mpa_id = ? " +
                "WHERE id = ?";

        jdbcTemplate.update(sqlQueryFilm,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );


        final String sqlQueryDelGenres = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlQueryDelGenres, film.getId());


        String sqlQueryGenres = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";



        if (film.getGenres() != null && film.getGenres().size() != 0) {

            List<Integer> genres = film.getGenres().stream()
                    .map(Genre::getId)
                    .distinct()
                    .collect(Collectors.toList());

            for (int i = 0; i < genres.size(); i++) {
                log.info("Отображение списка жанров {}", genres.size());
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
        String sqlQuery = "INSERT INTO likes ( FILM_ID, USER_ID)" +
                "VALUES ( ?, ?)";
        Film film = getById(id).orElseThrow(() -> new NotFoundException("Фильм с Id = " + id + " не существует!"));

        jdbcTemplate.update(sqlQuery, id, userId);

        log.info("Возвращены данные о фильме {}", getById(id).get().toString());
        return film;
    }





    @Override
    public List<Film> getAll() {
        final String sqlQuery = "SELECT * " +
                "FROM film " +
                "JOIN mpa ON film.mpa_id = mpa.id";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilmForGetAll);

        log.info("Метод getAll(). Возвращен список фильмов : {}", films.size());
        return films;
    }

/////////////////////////////////////////////////////////

    @Override
    public List<Film> getPopular(int top) {
        final String sqlQuery = "SELECT f.id, " +
                "f.name, " +
                "f.description, " +
                "f.realise_date, " +
                "f.duration, " +
                "f.mpa_id, " +
                "m.name, " +
                "count (user_id) " +
                "FROM film  AS f " +
                "left join likes AS l ON f.id = l.film_id " +
                "LEFT JOIN mpa as m ON f.mpa_id = m.id " +   // эта строка добавлеа 06.01.23
                "group by (f.id) " +
                "order by count (user_id) DESC " +
                "limit " + top;

        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilmForPop);
        log.info("Метод getPopular() Возвращен TOP {}", top);

        for (int i = 0; i < films.size(); i++) {
            films.get(i).setGenres(getGenresFromDB(films.get(i).getId()));

        }

        return films;
    }



    private Film makeFilmForPop(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("realise_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(new RatingMpa(resultSet.getInt("mpa_id"), resultSet.getString("mpa.name")))
                .genres(getGenresFromDB2(resultSet.getInt("id")))
                .build();
    }





    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film =  new Film(rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                LocalDate.parse(rs.getString("realise_date")),
                Long.parseLong(rs.getString("duration")),
                new RatingMpa(rs.getInt("mpa_id"), rs.getString("name")),
                null
        );
        log.info("makeFilm()  id = {}, mpa_id = {}, mpa+name = {}",film.getId(), film.getMpa().getId(), film.getMpa().getName());

        return film;
    }


}













//TODO удалить после прохождения всех тестов
/*
    @Override
    public List<Film> getPopular(int top) {
        final String sqlQuery = "SELECT f.id, " +
                "f.name, " +
                "f.description, " +
                "f.realise_date, " +
                "f.duration, " +
                "f.mpa_id, " +
                "count (user_id) " +
                "FROM film  AS f " +
                "left join likes AS l ON f.id = l.film_id " +
                "LEFT JOIN mpa as m ON f.mpa_id = m.id " +   // эта строка добавлеа 06.01.23
                "group by (f.id) " +
                "order by count (user_id) DESC " +
                "limit " + top;

        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm);
        log.info("Метод getPopular() Возвращен TOP {}", top);

        for (int i = 0; i < films.size(); i++) {
            films.get(i).setGenres(getGenresFromDB(films.get(i).getId()));

        }

        return films;
    }




    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film =  new Film(rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                LocalDate.parse(rs.getString("realise_date")),
                Long.parseLong(rs.getString("duration")),
                new RatingMpa(rs.getInt("mpa_id"), rs.getString("name")),
                null
        );
        log.info("makeFilm()  id = {}, mpa_id = {}, mpa+name = {}",film.getId(), film.getMpa().getId(), film.getMpa().getName());

        return film;
    }

 */