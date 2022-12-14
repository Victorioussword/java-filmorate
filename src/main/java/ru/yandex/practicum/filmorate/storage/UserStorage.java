package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserStorage {

    // старые методы
    Map<Long, User> getAll();  // все фильмы - get

    Optional<User> add(User user);  // добавляет фильм - post

    User update(User user);

   Optional <User>  getById(long id);

   List<User> getFriends(long id);


   //  Новые методы
    void addFriend (long userId, long friendId);

    User delFriendship (long userId, long friendId);


}