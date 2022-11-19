package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
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
    public User add(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
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
}
