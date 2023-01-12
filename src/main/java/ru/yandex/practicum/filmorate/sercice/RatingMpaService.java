package ru.yandex.practicum.filmorate.sercice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.reitingsnpa.RatingMpaDbStorage;

import java.util.Collection;


@Slf4j
@RequiredArgsConstructor
@Service
public class RatingMpaService {

    private final RatingMpaDbStorage ratingMpaStorage;

    public RatingMpa getById(int id) {
        RatingMpa ratingMpa = ratingMpaStorage.getById(id).orElseThrow(() -> new NotFoundException("Рейтинг с Id =" + id + " не существует"));
        return ratingMpa;
    }

    public Collection<RatingMpa> getAll() {
        log.info("GET /mpa. Количество рейтингов: {}", ratingMpaStorage.getAll().size());
        return ratingMpaStorage.getAll();
    }

}

