package ru.yandex.practicum.filmorate.storage.films;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.genres.GenreMapper;
import ru.yandex.practicum.filmorate.storage.reitingsnpa.RatingMPAMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class FilmMapper implements RowMapper<Film> {

    JdbcTemplate jdbcTemplate;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {


        return new Film( rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                LocalDate.parse(rs.getString("realise_date")),
                Long.parseLong(rs.getString("duration")),
                new RatingMpa(rs.getInt("id"), rs.getString("name")),
                null
        );


    }


    private RatingMpa makeRatingMpa(SqlRowSet ratingRows) {
        RatingMpa mpa = new RatingMpa(
                ratingRows.getInt("ID"),
                ratingRows.getString("NAME"));
        //log.info("Метод getMpaFromDb() {}", mpa.toString());
        return mpa;
    }


    private RatingMpa makeMpa(int id) {
        String sqlQuery = "SELECT *" +
                "FROM MPA" +
                "WHERE ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, new RatingMPAMapper(), id);
    }

    private List<Genre> getGenresFromDB(long Id) {   //  Ошибкав строке запроса
        List<Genre> genres;
        final String sqlQuery = "SELECT * " +
                "FROM GENRE " +
                "LEFT JOIN FILM_GENRE ON GENRE.ID = FILM_GENRE.GENRE_ID " +
                "WHERE FILM_ID = ?";
        genres = jdbcTemplate.query(sqlQuery, new GenreMapper(), Id);
        return genres;
    }
}

