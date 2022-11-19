package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.sercice.UserService;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<User> get() {
        return userService.getAll().values();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable long id) {
        return userService.getById(id);
    }

    @PostMapping
    public User post(@Valid @RequestBody User user) {
        loginCheck(user);
        emptyNameCheck(user);
        return userService.add(user);
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        loginCheck(user);
        checkId(user.getId());
        emptyNameCheck(user);
        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable long id, @PathVariable long friendId) {
        checkId(id);
        checkId(friendId);
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User dellById(@PathVariable long id, @PathVariable long friendId) {
        checkId(id);
        checkId(friendId);
        return userService.dellFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable long id) {
        checkId(id);
        return userService.getFriends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        checkId(id);
        checkId(otherId);
        return userService.getCommonFriends(id, otherId);
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
        if (!userService.getAll().containsKey(id)) {
            log.info("Пользователь не существует {}", id);
            throw new NotFoundException("Пользователь не существует");
        }
    }
}

