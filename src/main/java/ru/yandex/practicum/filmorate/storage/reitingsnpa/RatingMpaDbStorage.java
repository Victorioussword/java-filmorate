package ru.yandex.practicum.filmorate.storage.reitingsnpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
@Service
public class RatingMpaDbStorage implements RatingMpaStorage {

    private final  JdbcTemplate jdbcTemplate;

    public List<RatingMpa> getAll() {
        final String sqlQuery = "SELECT * "
                + "FROM mpa ";
        List<RatingMpa> ratingMpa = jdbcTemplate.query(sqlQuery, new RatingMPAMapper());
        return ratingMpa;
    }

    public Optional<RatingMpa> getById(int id) {
        final String sqlQuery = "SELECT " +
                "id, " +
                "name " +
                "FROM mpa "+
                "WHERE id = ? ";

        SqlRowSet ratingRows = jdbcTemplate.queryForRowSet(sqlQuery, new Object[]{id});
        if (ratingRows.next()) {
            RatingMpa ratingMpa = makeRatingMpa(ratingRows);
            log.info("Найден рейтинг: {} {}", ratingMpa.getId(), ratingMpa.getName());
            return Optional.of(ratingMpa);
        } else {
            log.info("Рейтинг с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    private RatingMpa makeRatingMpa(SqlRowSet ratingMpaRows) {
        RatingMpa ratingMpa = new RatingMpa(
                ratingMpaRows.getInt(1),
                ratingMpaRows.getString(2));
        return ratingMpa;
    }
}



