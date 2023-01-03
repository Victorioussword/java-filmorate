package ru.yandex.practicum.filmorate.storage.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;


@Slf4j
@RequiredArgsConstructor
@Component("usersInDB")
@Primary
@Repository
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;


    @Override
    public Optional<User> add (User user) {
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

        return Optional.of(user);
    }



    @Override
    public List<User> getAll() {
        final String sqlQuery = "SELECT *"
                + " FROM USERS";

        List<User> users = jdbcTemplate.query(sqlQuery, new UserMapper());
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
           // return Optional.empty();
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

        getById(userId);
        getById(friendId);

        String sqlQuery = "INSERT INTO FRIENDSHIP (USER_ID,  FRIEND_ID) " +
                " VALUES (?, ?)";
        log.info("ДОбавление в друзья. Запрос: {}", jdbcTemplate.update(sqlQuery, userId, friendId));
       // jdbcTemplate.update(sqlQuery, userId, friendId);
    }



    public List<User> getFriends(long id) {
        getById(id).orElseThrow(() -> new NotFoundException("Пользователь с Id = " + id + " не обнаружен"));
        final String sqlQuery2 = "SELECT *" +
                " FROM FRIENDSHIP " +
                " JOIN USERS ON USERS.ID = FRIENDSHIP.FRIEND_ID " +
                " WHERE FRIENDSHIP.USER_ID = ?";

        List<User> users = jdbcTemplate.query(sqlQuery2, new UserMapper(), id);
        log.info("Возвращен список друзей пользователя : {}", users.size());

        return users;
    }


    @Override
    public User delFriendship(long userId, long friendId) {
        User user = getById(userId).orElseThrow(() -> new NotFoundException("Пользователь с Id = " + userId + " не обнаружен"));
        User user2 = getById(friendId).orElseThrow(() -> new NotFoundException("Друг с Id = " + friendId + " не обнаружен"));
        final String sqlQuery = "DELETE " +
                "FROM FRIENDSHIP " +
                "WHERE " +
                " USER_ID = ? AND FRIEND_ID = ? ";
        jdbcTemplate.update(sqlQuery, userId, friendId);

        return user;
    }


    @Override
    public List<User> getCommonFriends(long id, long otherId) {
     User user = getById(id).orElseThrow(() -> new NotFoundException("Пользователь с Id = " + id + " не обнаружен"));
        User user2 = getById(otherId).orElseThrow(() -> new NotFoundException("Друг с Id = " + otherId + " не обнаружен"));

        String sqlQuery =  "SELECT " +
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

        return jdbcTemplate.query(sqlQuery, new UserMapper(), id, otherId);
    }



    public User deleteById(long id) {
        final String sqlQuery = "DELETE FROM users WHERE id = ?";
        User user = getById(id).orElseThrow(() -> new NotFoundException("Пользователь с Id = " + id + " не обнаружен"));
        jdbcTemplate.update(sqlQuery, id);
        log.info("Пользователь с id {} удален", id);
        return user;
    }

    private User makeUser(SqlRowSet userRows) {
        return new User(
                Long.parseLong(userRows.getString("ID")),
                userRows.getString("EMAIL"),
                userRows.getString("LOGIN"),
                userRows.getString("NAME"),
                LocalDate.parse(userRows.getString("BIRTHDAY")));
    }

    private void checkFriendship(long userId, long friendId) {
        User userOne = getById(userId).orElseThrow(() -> new NotFoundException("Пользователь с Id = " + userId + " не обнаружен"));
        User userTwo = getById(friendId).orElseThrow(() -> new NotFoundException("Пользователь с Id = " + friendId + " не обнаружен"));
    }

}
