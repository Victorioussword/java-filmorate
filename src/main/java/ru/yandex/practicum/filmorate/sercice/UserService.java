package ru.yandex.practicum.filmorate.sercice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.users.UserDbStorage;

import java.util.*;


@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserDbStorage userStorage;

    public List<User> getAll() {
        log.info("GET /users. Количество пользователей: {}", userStorage.getAll().size());
        return userStorage.getAll();
    }

    public User add(User user) {
        User user1 = userStorage.add(user).orElseThrow(() -> new NotFoundException("Ошибка добавления пользователя"));
        log.info("Добавлен user: {}", user1);
        return user1;
    }

    public User update(User user) {
        checkId(user.getId());
        userStorage.update(user);
        log.info("PUT /users. Обновлены данные пользователя {}", user.getId());
        return user;
    }

    public User getById(long id) {
        User user = userStorage.getById(id).orElseThrow(() -> new NotFoundException("Пользователь с Id = " + id + " не обнаружен"));
        log.info("Возвращены данные о пользователе {}", user.toString());
        return user;
    }

    public User addFriend(long friendOneId, long friendTwoId) {
        userStorage.addFriend(friendOneId, friendTwoId);
        User user = userStorage.getById(friendOneId).orElseThrow(() -> new NotFoundException("Пользователь с Id = " + friendOneId + " не обнаружен"));
        return user;
    }

    public User dellFriendship(long friendOneId, long friendTwoId) {
        userStorage.delFriendship(friendOneId, friendTwoId);
        User user = userStorage.getById(friendOneId).orElseThrow(() -> new NotFoundException("Пользователь с Id = " + friendOneId + " не обнаружен"));
        return user;
    }

    public List<User> getFriends(long id) {
        return userStorage.getFriends(id);
    }

    private void checkId(long id) {
        Map<Long, User> users = new HashMap<>();
        List<User> us = userStorage.getAll();
        for (int i = 0; i < us.size(); i++) {
            users.put(us.get(i).getId(), us.get(i));
        }

        if (!users.containsKey(id)) {
            log.info("Пользователь не существует {}", id);
            throw new NotFoundException ("Пользователь с Id " + id + " не существует.");
        }
    }


    public List<User> getCommonFriends(long id, long otherId) {
       return userStorage.getCommonFriends (id, otherId);
    }
}
