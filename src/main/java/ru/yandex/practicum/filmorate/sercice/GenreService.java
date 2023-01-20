package ru.yandex.practicum.filmorate.sercice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genres.GenreStorage;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GenreService {

    private final GenreStorage genreStorage;

    public Genre getById(int id) {
        Genre genre = genreStorage.getById(id).orElseThrow(() -> new NotFoundException("Жанр с Id =" + id + " не существует"));
        log.info("GET /genres. Возвращен жанр: {}", genre.getName());
        return genre;
    }

    public List<Genre> getAll() {
        List<Genre> genres = genreStorage.getAll();
        log.info("GET /genres. Количество жанров: {}", genres.size());
        return genres;
    }
}
