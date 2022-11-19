package ru.yandex.practicum.filmorate.sercice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;


import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {


    private final UserStorage inMemoryUserStorage;

    public Map<Long, User> getAll() {
        log.info("GET /users. Количество пользователей: {}", inMemoryUserStorage.getAll().size());
        return inMemoryUserStorage.getAll();
    }

    public User add(User user) {

        inMemoryUserStorage.add(user);
        log.info("Добавлен user: {}", user);
        return user;
    }

    public User update(User user) {

        inMemoryUserStorage.update(user);
        log.info("PUT /users. Обновлены данные пользователя {}", user.getId());
        return user;
    }

    public User getById(long id) {
        Optional<User> userOpt = inMemoryUserStorage.getById(id);
        User user = userOpt.orElseThrow(() -> new NotFoundException("Пользователь с Id = " + id + " не обнаружен"));
        log.info("Возвращены данные о фильме {}", userOpt.get().toString());
        return user;


        // if (!inMemoryUserStorage.getAll().containsKey(id)) {
        //     throw new NotFoundException("Пользователь с Id = " + id + " не существует!");
        //   }
        //  return inMemoryUserStorage.getById(id);
    }

    public User addFriend(long friendOneId, long friendTwoId) {
        Optional<User> userOptOne = inMemoryUserStorage.getById(friendOneId);
        Optional<User> userOptTwo = inMemoryUserStorage.getById(friendTwoId);

        User userOne = userOptOne.orElseThrow(() -> new NotFoundException("Пользователь с Id = " + friendOneId + " не обнаружен"));
        User userTwo = userOptTwo.orElseThrow(() -> new NotFoundException("Пользователь с Id = " + friendTwoId + " не обнаружен"));

        userOne.getFriends().add(friendTwoId);
        userTwo.getFriends().add(friendOneId);

        inMemoryUserStorage.update(userOne);
        inMemoryUserStorage.update(userTwo);

        return userOne;
    }

    public User dellFriend(long friendOneId, long friendTwoId) {

        Optional<User> userOptOne = inMemoryUserStorage.getById(friendOneId);
        Optional<User> userOptTwo = inMemoryUserStorage.getById(friendTwoId);

        User userOne = userOptOne.orElseThrow(() -> new NotFoundException("Пользователь с Id = " + friendOneId + " не обнаружен"));
        User userTwo = userOptTwo.orElseThrow(() -> new NotFoundException("Пользователь с Id = " + friendTwoId + " не обнаружен"));

        userOne.getFriends().remove(friendTwoId);
        userTwo.getFriends().remove(friendOneId);

        inMemoryUserStorage.update(userOne);
        inMemoryUserStorage.update(userTwo);

        return userOne;
    }

    public List< User> getFriends(long id) {

        Optional<User> userOptOne = inMemoryUserStorage.getById(id);
        User userOne = userOptOne.orElseThrow(() -> new NotFoundException("Пользователь с Id = " + id + " не обнаружен"));

        List< User> friends = new ArrayList<>();
        for (Long idFriend : userOne.getFriends()) {
            friends.add(inMemoryUserStorage.getById(idFriend).get());
        }
        return friends;
    }

    public List<User> getCommonFriends(long friendOneId, long friendTwoId) {

        Optional<User> userOptOne = inMemoryUserStorage.getById(friendOneId);
        Optional<User> userOptTwo = inMemoryUserStorage.getById(friendTwoId);

        User userOne = userOptOne.orElseThrow(() -> new NotFoundException("Пользователь с Id = " + friendOneId + " не обнаружен"));
        User userTwo = userOptTwo.orElseThrow(() -> new NotFoundException("Пользователь с Id = " + friendTwoId + " не обнаружен"));

        Set<Long> friendsOne = userOne.getFriends();
        Set<Long> friendsTwo = userTwo.getFriends();

        List<User> commonFriends = new ArrayList<>();

        for (long frIdOne : friendsOne) {
            for (long frIdTwo : friendsTwo) {
                if (frIdOne == frIdTwo) {
                    commonFriends.add(inMemoryUserStorage.getById(frIdOne).get());
                }
            }
        }
        return commonFriends;
    }
}
