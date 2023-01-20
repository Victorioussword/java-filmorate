package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.users.UserMapper;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;


    @Override
    public void addFriend(long userId, long friendId) {
        String sqlQuery = "INSERT INTO FRIENDSHIP (USER_ID,  FRIEND_ID) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        log.info("ДОбавление в друзья выполнено. User {}, Friend {}", userId, friendId);
    }


    @Override
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
}
