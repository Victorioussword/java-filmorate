package ru.yandex.practicum.filmorate.sercice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genres.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.users.UserDbStorage;

import java.util.Collection;


@Slf4j
@RequiredArgsConstructor
@Service
public class GenreService {

    private final GenreDbStorage genreStorage;

    public Genre getById(int id) {
        Genre genre = genreStorage.getById(id).orElseThrow(() -> new NotFoundException("Жанр с Id =" + id + " не существует"));
        return genre;
    }


    public Collection<Genre> getAll() {
        log.info("GET /genres. Количество жанров: {}", genreStorage.getAll().size());
        return genreStorage.getAll();
    }
}
