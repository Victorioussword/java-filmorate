package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.sercice.GenreService;
import ru.yandex.practicum.filmorate.sercice.RatingMpaService;

import java.util.Collection;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class ReitingsMpaConroller {

    private final RatingMpaService ratingMpaService;

    @GetMapping
    public Collection<RatingMpa> get() {
        return ratingMpaService.getAll();
    }

    @GetMapping("/{id}")
    public RatingMpa getById(@PathVariable int id) {
        return ratingMpaService.getById(id);
    }


}
