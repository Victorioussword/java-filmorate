package ru.yandex.practicum.filmorate.storage.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component("usersInDB")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    @Override
    public User add(User user) {
        final String sqlQuery = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) " +
                "VALUES ( ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            final PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, new String[]{"ID"});
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getLogin());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setDate(4, Date.valueOf(user.getBirthday()));
            return preparedStatement;
        }, keyHolder);

        log.info("Пользователь с id {} добавлен", user.getId());
        user.setId(keyHolder.getKey().intValue());
        return user;
    }


    @Override
    public List<User> getAll() {
        final String sqlQuery = "SELECT *"
                + " FROM USERS";
        List<User> users = jdbcTemplate.query(sqlQuery, userMapper);
        log.info("Возвращен список позьзователей : {}", users.size());
        return users;
    }

    @Override
    public Optional<User> getById(long id) {

        final String sqlQuery = "SELECT *"
                + " FROM USERS " +
                "WHERE ID = ?";

        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userRows.next()) {
            User user = makeUser(userRows);
            log.info("Найден пользователь: {} {}", user.getId(), user.getName());
            return Optional.of(user);

        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Override
    public User update(User user) {
        final String sqlQuery = "UPDATE USERS SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? " +
                "WHERE ID = ?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        log.info("Обновление данных о пользователе {} выполнено.", user.getId());
        return user;
    }

    public void addFriend(long userId, long friendId) {
         String sqlQuery = "INSERT INTO FRIENDSHIP (USER_ID,  FRIEND_ID) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        log.info("ДОбавление в друзья выполнено. User {}, Friend {}", userId, friendId);
    }

    public List<User> getFriends(long id) {
               final String sqlQuery2 = "SELECT *" +
                " FROM FRIENDSHIP " +
                " JOIN USERS ON USERS.ID = FRIENDSHIP.FRIEND_ID " +
                " WHERE FRIENDSHIP.USER_ID = ?";

        List<User> users = jdbcTemplate.query(sqlQuery2, userMapper, id);
        log.info("Возвращен список друзей пользователя : {}", users.size());
        return users;
    }

    @Override
    public void delFriendship(long userId, long friendId) {
        final String sqlQuery = "DELETE " +
                "FROM FRIENDSHIP " +
                "WHERE " +
                "USER_ID = ? AND FRIEND_ID = ? ";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        return;
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) {

        String sqlQuery = "SELECT " +
                "USERS.ID, " +
                "USERS.EMAIL, " +
                "USERS.LOGIN, " +
                "USERS.NAME, " +
                "USERS.BIRTHDAY " +
                "FROM FRIENDSHIP " +
                "JOIN USERS ON USERS.ID = FRIENDSHIP.FRIEND_ID " +
                "WHERE FRIENDSHIP.USER_ID = ? AND " +
                "FRIENDSHIP.FRIEND_ID IN (" +
                "SELECT FRIEND_ID " +
                "FROM FRIENDSHIP " +
                "WHERE USER_ID = ?)";

        return jdbcTemplate.query(sqlQuery, userMapper, id, otherId);
    }

    private User makeUser(SqlRowSet userRows) {
        return new User(
                Long.parseLong(userRows.getString("id")),
                userRows.getString("email"),
                userRows.getString("login"),
                userRows.getString("name"),
                LocalDate.parse(userRows.getString("birthday")));
    }
}
