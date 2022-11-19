package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;
import java.util.Optional;

public interface UserStorage {

    // старые методы
    Map<Long, User> getAll();  // все фильмы - get

    User add(User user);  // добавляет фильм - post

    User update(User user);

    // новый методы
   Optional <User>  getById(long id);


}
