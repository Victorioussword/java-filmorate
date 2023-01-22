package ru.yandex.practicum.filmorate.storage.reitingsnpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Slf4j
public class RatingMPAMapper implements RowMapper<RatingMpa> {

    @Override
    public RatingMpa mapRow(ResultSet rs, int rowNum) throws SQLException {
        RatingMpa mpa = new RatingMpa(rs.getInt("id"),
                rs.getString("name")
        );
        log.info("Метод mapRow(), ID =  {}, c name = {}", mpa.getId(), mpa.getName());
        return mpa;
    }
}

