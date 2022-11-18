package ru.yandex.practicum.filmorate.sercice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {


    private final UserStorage inMemoryUserStorage;

    public Map< Long , User> getAll() {
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
        if (!inMemoryUserStorage.getAll().containsKey(id)) {
            throw new NotFoundException("Пользователь с Id = " + id + " не существует!");
        }
        return inMemoryUserStorage.getById(id);
    }

    public User addFriend(long friendOneId, long friendTwoId) {

        inMemoryUserStorage.getById(friendOneId).getFriends().add(friendTwoId);
        inMemoryUserStorage.getById(friendTwoId).getFriends().add(friendOneId);
        return inMemoryUserStorage.getById(friendOneId);
    }

    public User dellFriend(long friendOneId, long friendTwoId) {

        inMemoryUserStorage.getById(friendOneId).getFriends().remove(friendTwoId);
        inMemoryUserStorage.getById(friendTwoId).getFriends().remove(friendOneId);
        return inMemoryUserStorage.getById(friendOneId);
    }

    public Collection<User> getFriends(long id) {

        Map<Long, User> friends = new HashMap<>();
        for (Long idFriend : inMemoryUserStorage.getById(id).getFriends()) {
            friends.put(idFriend, inMemoryUserStorage.getById(idFriend));
        }
        return friends.values();
    }

    public Collection<User> getCommonFriends(long friendOneId, long friendTwoId) {

        Set<Long> friendsOne = inMemoryUserStorage.getById(friendOneId).getFriends();
        Set<Long> friendsTwo = inMemoryUserStorage.getById(friendTwoId).getFriends();
        return friendsOne.stream().filter(friendsTwo::contains)
                .map(inMemoryUserStorage::getById)
                .collect(Collectors.toList());
    }


}
