package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.sercice.UserService;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> get() {
        return userService.getAll();
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
        emptyNameCheck(user);
        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable long id, @PathVariable long friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User dellFriendshipById(@PathVariable long id, @PathVariable long friendId) {
        return userService.dellFriendship(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable long id) {
        return userService.getFriends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
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
}

