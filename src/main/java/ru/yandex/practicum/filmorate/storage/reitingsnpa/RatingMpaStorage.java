package ru.yandex.practicum.filmorate.storage.reitingsnpa;

import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.List;
import java.util.Optional;

public interface RatingMpaStorage {
    List<RatingMpa> getAll();

    Optional<RatingMpa> getById(int id);
}
