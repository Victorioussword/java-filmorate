package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;


    public List<User> getAll() {
        log.info("GET /users. Количество пользователей: {}", userStorage.getAll().size());
        return userStorage.getAll();
    }

    public User add(User user) {
        User user1 = userStorage.add(user);
        log.info("Добавлен user: {}", user1);
        return user1;
    }

    public User update(User user) {
        userStorage.getById(user.getId());
        userStorage.update(user);
        log.info("PUT /users. Обновлены данные пользователя {}", user.getId());
        return user;
    }

    public User getById(long id) {
        User user = userStorage.getById(id);
        log.info("Возвращены данные о пользователе {}", user.toString());
        return user;
    }

    public User addFriend(long friendOneId, long friendTwoId) {
        User user = userStorage.getById(friendOneId);
        userStorage.getById(friendTwoId);
        friendshipStorage.addFriend(friendOneId, friendTwoId);
        return user;
    }

    public User dellFriendship(long friendOneId, long friendTwoId) {
        friendshipStorage.delFriendship(friendOneId, friendTwoId);
        User user = userStorage.getById(friendOneId);
        userStorage.getById(friendTwoId);
        return user;
    }

    public List<User> getFriends(long id) {
        userStorage.getById(id);
        return friendshipStorage.getFriends(id);
    }

    public List<User> getCommonFriends(long id, long otherId) {
        userStorage.getById(id);
        userStorage.getById(otherId);
        return friendshipStorage.getCommonFriends(id, otherId);
    }
}
