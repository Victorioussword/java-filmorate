package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;
import java.util.Optional;

public interface FilmStorage {

    // старые методы
    Map<Long, Film> getAll();  // все фильмы - get

    Film add(Film film);  // добавляет фильм - post

   Film update(Film film);  // обновляет фильм - put


   // новый методы
    Optional <Film>  getById(long id);


}
