package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class FriendsDbStorage {

    private final JdbcTemplate jdbc;

    public void addFriend(int userId, int friendId) {
        // Проверяем, что дружба ещё не существует
        if (!getFriends(userId).contains(friendId)) {
            String sql = "INSERT INTO friends(user_id, friend_id) VALUES (?, ?)";
            jdbc.update(sql, userId, friendId);
        }
    }

    public void removeFriend(int userId, int friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        int rows = jdbc.update(sql, userId, friendId);
        if (rows == 0) {
            throw new NotFoundException("Дружба между пользователями не найдена");
        }
    }

    public Collection<Integer> getFriends(int userId) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ?";
        return jdbc.query(sql,
                (rs, rowNum) -> rs.getInt("friend_id"),
                userId);
    }

    public Collection<Integer> getCommonFriends(int userId, int otherId) {
        String sql = """
            SELECT f1.friend_id
            FROM friends f1
            JOIN friends f2 ON f1.friend_id = f2.friend_id
            WHERE f1.user_id = ? AND f2.user_id = ?
        """;
        return jdbc.query(sql,
                (rs, rowNum) -> rs.getInt("friend_id"),
                userId,
                otherId);
    }
}