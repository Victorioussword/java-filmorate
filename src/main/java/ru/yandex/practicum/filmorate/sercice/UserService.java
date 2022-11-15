package ru.yandex.practicum.filmorate.sercice;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private int id = 1;

    private final InMemoryUserStorage inMemoryUserStorage;

    public Collection<User> getAll() {
        log.info("GET /users. Количество пользователей: {}", inMemoryUserStorage.getAll().size());
        return inMemoryUserStorage.getAll().values();
    }

    public User add(@Valid @RequestBody User user) {
        loginCheck(user);
        user.setId(id++);
        emptyNameCheck(user);
        inMemoryUserStorage.add(user);
        log.info("Добавлен user: {}", user);
        return user;
    }

    public User update(@Valid @RequestBody User user) {
        loginCheck(user);
        checkId(user.getId());
        emptyNameCheck(user);
        inMemoryUserStorage.update(user);
        log.info("PUT /users. Обновлены данные пользователя {}", user.getId());
        return user;
    }

    public User getById(long id) {
        if (!inMemoryUserStorage.getAll().containsKey(id)) {
            throw new NotFoundException("Пользователь с Id = " + id + " не существует!");
        }
        return inMemoryUserStorage.getById(id);
    }

    public User addFriend(long friendOneId, long friendTwoId) {
        checkId(friendOneId);
        checkId(friendTwoId);
        inMemoryUserStorage.getById(friendOneId).getFriends().add(friendTwoId);
        inMemoryUserStorage.getById(friendTwoId).getFriends().add(friendOneId);
        return inMemoryUserStorage.getById(friendOneId);
    }

    public User dellFriend(long friendOneId, long friendTwoId) {
        checkId(friendOneId);
        checkId(friendTwoId);
        inMemoryUserStorage.getById(friendOneId).getFriends().remove(friendTwoId);
        inMemoryUserStorage.getById(friendTwoId).getFriends().remove(friendOneId);
        return inMemoryUserStorage.getById(friendOneId);
    }

    public Collection<User> getFriends(long id) {
        checkId(id);
        Map<Long, User> friends = new HashMap<>();
        for (Long idFriend : inMemoryUserStorage.getById(id).getFriends()) {
            friends.put(idFriend, inMemoryUserStorage.getById(idFriend));
        }
        return friends.values();
    }

    public Collection<User> getCommonFriends(long friendOneId, long friendTwoId) {
        checkId(friendOneId);
        checkId(friendTwoId);
        Set<Long> friendsOne = inMemoryUserStorage.getById(friendOneId).getFriends();
        Set<Long> friendsTwo = inMemoryUserStorage.getById(friendTwoId).getFriends();
        return friendsOne.stream().filter(friendsTwo::contains)
                .map(inMemoryUserStorage::getById)
                .collect(Collectors.toList());
    }

    private void loginCheck(User user) {
        String login = user.getLogin();
        if (login.contains(" ")) {
            log.info("Указан не корректный логин. Введено {}", user.getLogin());
            throw new ValidationException("Указан не корректный логин");
        }
    }

    private void emptyNameCheck(User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
            log.info("Поле name пустое - в качестве name использован логин {}", user.getLogin());
        }
    }

    private void checkId(long id) {
        if (!inMemoryUserStorage.getAll().containsKey(id)) {
            log.info("Пользователь не существует {}", id);
            throw new NotFoundException("Пользователь не существует");
        }
    }
}
