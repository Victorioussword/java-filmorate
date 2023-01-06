package ru.yandex.practicum.filmorate.storage.films;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    // старые методы
    List<Film> getAll();  // все фильмы - get

    Film add(Film film);  // добавляет фильм - post

   Film update(Film film);  // обновляет фильм - put


   // новый методы
    Optional <Film>  getById(long id);

    Film addLike(long id, long userId);

    List<Film> getPopular(int top);

    void delLike(long id, long userId);
}
