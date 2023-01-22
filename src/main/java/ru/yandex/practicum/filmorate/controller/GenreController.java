package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;


@Slf4j   //добавлено логирование
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {

        private final GenreService genreService;

        @GetMapping
        public List<Genre> get() {
            List<Genre> genres = genreService.getAll();
            log.info("GenreController. Метод get(). Размер возвращенного списка жанров = {}.", genres.size());
            return genres;
        }

        @GetMapping("/{id}")
        public Genre getById(@PathVariable int id) {
            Genre genre = genreService.getById(id);
            log.info("GenreController. Метод getById(). Возвращен жанр id = {}, name = {}.", genre.getId(), genre.getName());
            return genre;
        }
}
