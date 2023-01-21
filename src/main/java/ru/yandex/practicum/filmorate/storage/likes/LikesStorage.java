package ru.yandex.practicum.filmorate.storage.likes;

import ru.yandex.practicum.filmorate.model.Film;


public interface LikesStorage {

    Film addLike(long id, long userId);

    void delLike(long id, long userId);
}
