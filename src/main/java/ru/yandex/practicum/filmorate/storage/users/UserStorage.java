package ru.yandex.practicum.filmorate.storage.users;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<User> getAll();

    Optional<User> add(User user);

    User update(User user);

   Optional <User>  getById(long id);

   List<User> getFriends(long id);

    void addFriend (long userId, long friendId);

    User delFriendship (long userId, long friendId);


    List<User> getCommonFriends(long id, long otherId);


}