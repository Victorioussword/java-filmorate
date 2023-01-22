package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.reitingsnpa.RatingMpaStorage;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class RatingMpaService {

    private final RatingMpaStorage ratingMpaStorage;

    public RatingMpa getById(int id) {
        RatingMpa ratingMpa = ratingMpaStorage.getById(id).orElseThrow(() -> new NotFoundException("Рейтинг с Id =" + id + " не существует"));
        return ratingMpa;
    }

    public List<RatingMpa> getAll() {
        List <RatingMpa> ratings = ratingMpaStorage.getAll();
        log.info("GET /mpa. Количество рейтингов: {}", ratings.size());
        return ratings;
    }

}

