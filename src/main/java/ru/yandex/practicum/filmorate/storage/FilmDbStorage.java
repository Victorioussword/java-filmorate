package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Component ("filmsInDB")
@Primary
public class FilmDbStorage implements FilmStorage {


    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Film> getById(long id) {

        return Optional.empty();
    }



    @Override
    public Map<Long, Film> getAll() {
        return null;
    }
// TODO пример на 8:38

    // Методы для доработки ниже



    @Override
    public Film add(Film film) {
        return null;
    }

    @Override
    public Film update(Film film) {
        return null;
    }


}
