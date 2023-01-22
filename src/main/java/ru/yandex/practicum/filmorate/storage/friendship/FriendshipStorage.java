package ru.yandex.practicum.filmorate.storage.friendship;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendshipStorage {

    List<User> getFriends(long id);

    void addFriend (long userId, long friendId);

    void delFriendship (long userId, long friendId);

    List<User> getCommonFriends(long id, long otherId);
}
