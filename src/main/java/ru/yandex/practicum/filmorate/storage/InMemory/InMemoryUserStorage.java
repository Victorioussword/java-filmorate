package ru.yandex.practicum.filmorate.storage.InMemory;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// TODO удалить всю реализацию в памяти

@Component
public class InMemoryUserStorage implements UserStorage {

    private int id = 1;

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Map<Long, User> getAll() {
        Map<Long, User> forReturn = new HashMap<>();
        forReturn.putAll(users);
        return forReturn;
    }

    @Override
    public Optional<User> add(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        return Optional.of( user);
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getById(long id) {
        if (!users.containsKey(id)) {
            return Optional.empty();
        } else {
            return Optional.of(users.get(id));
        }
    }


    @Override
    public List<User> getFriends(long id) {
        return null;
    }


    // Метод не используется - для реализации интерфейса
    @Override
    public void addFriend(long userId, long friendId) {

    }

    @Override
    public User delFriendship(long userId, long friendId) {
        return null;
    }


}
