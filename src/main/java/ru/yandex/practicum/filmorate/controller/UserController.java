package ru.yandex.practicum.filmorate.controller;


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

@RestController
@RequestMapping("/users")
public class UserController {
    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private Map<Integer, User> users = new HashMap<>();
    int id = 1;

    @GetMapping
    public Collection<User> getUsers() {
        log.info("GET /users. Количество пользователей: {}", users.size());
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        return users.values();
    }

    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        ageCheck(user);
        loginCheck(user);
        user.setId(id++);
        User userOkName = emptyNameCheck(user);
        users.put(userOkName.getId(), userOkName);
        log.info("POST /users. Добавлен user: {}", user);
        return user;
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User user) {
        ageCheck(user);
        loginCheck(user);
        checkId(user);
        User userOkName = emptyNameCheck(user);
        users.put(userOkName.getId(), userOkName);
        log.info("PUT /users. ДОбновлены данные пользователя {}", users.size());
        return userOkName;
    }


    private void ageCheck(@Valid @RequestBody User user) {
        LocalDate currentDay = LocalDate.now();
        if (currentDay.isBefore(user.getBirthday())) {
            log.info("Указана не корректная дата рождения. Введено {}", user.getBirthday());
            throw new ValidationException("Указана не корректная дата рождения");
        }
    }

    private void loginCheck(@Valid @RequestBody User user) {
        String login = user.getLogin();
        if (login.contains(" ")) {
            log.info("Указан не корректный логин. Введено {}", user.getLogin());
            throw new ValidationException("Указан не корректный логин");
        }
    }

    private User emptyNameCheck(@Valid @RequestBody User user) {
        String name = user.getName();
        if (name == null || name.isEmpty()) {
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