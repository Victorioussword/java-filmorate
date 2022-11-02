package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private Map<Long, User> users = new HashMap<>();
    private int id = 1;

    @GetMapping
    public Collection<User> getUsers() {
        log.info("GET /users. Количество пользователей: {}", users.size());
        return users.values();
    }

    @PostMapping
    public User postUser(@Valid @RequestBody User user) {

        loginCheck(user);
        user.setId(id++);
        User userOkName = emptyNameCheck(user);
        users.put(userOkName.getId(), userOkName);
        log.info("POST /users. Добавлен user: {}", user);
        return user;
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User user) {

        loginCheck(user);
        checkId(user);
        User userOkName = emptyNameCheck(user);
        users.put(userOkName.getId(), userOkName);
        log.info("PUT /users. Обновлены данные пользователя {}", user.getId());
        return userOkName;
    }









    private void loginCheck( User user) {
        String login = user.getLogin();
        if (login.contains(" ")) {
            log.info("Указан не корректный логин. Введено {}", user.getLogin());
            throw new ValidationException("Указан не корректный логин");
        }
    }

    private User emptyNameCheck( User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
            log.info("Поле name пустое - в качестве name использован логин {}", user.getLogin());
        }
        return user;
    }

    private void checkId(User user) {
        if (!users.containsKey(user.getId())) {
            log.info("Обновление не существующего пользователя {}", user.toString());
            throw new ValidationException("Обновление данных не возможно - пользователь не существует");
        }
    }

}