package ru.yandex.practicum.filmorate.sercice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserStorage userStorage;
    //private  final UserDbStorage userStorage;

    public Map<Long, User> getAll() {
        log.info("GET /users. Количество пользователей: {}", userStorage.getAll().size());
        return userStorage.getAll();
    }

    public User add(User user) {
        User user1 =     userStorage.add(user).orElseThrow(() -> new NotFoundException("______________БЕДА В БАЗЕ_______"));;
        log.info("Добавлен user: {}", user);
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
        log.info("Возвращены данные о фильме {}", user.toString());
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

    // public List<User> getCommonFriends(long friendOneId, long friendTwoId) {
    //   User userOne = getById(friendOneId);
    //   User userTwo = getById(friendTwoId);
    //   return userOne.getFriends().stream()
    //          .filter(userTwo.getFriends()::contains)
    //         .map(this::getById).collect(Collectors.toList());
//    }

    private void checkId(long id) {
        if (!userStorage.getAll().containsKey(id)) {
            log.info("Пользователь не существует {}", id);
            throw new MethodArgumentNotValidException("Пользователь с Id " + id + " не существует.");
        }
    }
}
