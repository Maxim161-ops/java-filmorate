package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendsDbStorage {

    private final JdbcTemplate jdbcTemplate;

    // Добавление друга (двусторонняя дружба)
    public void addFriend(int userId, int friendId) {
        String sql = "MERGE INTO friends (user_id, friend_id) KEY(user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        String sql = "DELETE FROM friends WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        int rows = jdbcTemplate.update(sql, userId, friendId, friendId, userId);
    }

    // Получить список друзей пользователя
    public List<Integer> getFriends(int userId) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, userId);
    }

    // Получить список общих друзей двух пользователей
    public List<Integer> getCommonFriends(int userId, int otherId) {
        String sql = "SELECT f1.friend_id " +
                "FROM friends f1 " +
                "JOIN friends f2 ON f1.friend_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, userId, otherId);
    }
}