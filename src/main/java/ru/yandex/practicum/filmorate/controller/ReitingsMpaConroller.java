package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.service.RatingMpaService;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class ReitingsMpaConroller {

    private final RatingMpaService ratingMpaService;

    @GetMapping
    public List<RatingMpa> get() {
        List<RatingMpa> mpas = ratingMpaService.getAll();
        log.info("ReitingsMpaConroller. Метод get(). Размер возвращенного списка mpa = {}.", mpas.size());
        return mpas;
    }

    @GetMapping("/{id}")
    public RatingMpa getById(@PathVariable int id) {
        RatingMpa mpa = ratingMpaService.getById(id);
        log.info("ReitingsMpaConroller. Метод getById(). ВОзвращен  mpa = {}.", mpa.getName());
        return mpa;
    }
}
