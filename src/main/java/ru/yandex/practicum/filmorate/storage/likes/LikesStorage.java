package ru.yandex.practicum.filmorate.storage.likes;


public interface LikesStorage {

    void addLike(long id, long userId);

    void delLike(long id, long userId);
}
