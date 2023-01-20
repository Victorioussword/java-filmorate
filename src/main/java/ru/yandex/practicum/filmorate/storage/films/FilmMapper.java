package ru.yandex.practicum.filmorate.storage.films;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@RequiredArgsConstructor
public class FilmMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {

        return new Film(rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                LocalDate.parse(rs.getString("realise_date")),
                Long.parseLong(rs.getString("duration")),
                new RatingMpa(rs.getInt("id"), rs.getString("name")),
                null
        );
    }
}

