package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendsDbStorage implements FriendsStorage {

    private final JdbcTemplate jdbc;

    @Override
    public void addFriend(int userId, int friendId) {
        // Добавляем дружбу в обе стороны
        jdbc.update("INSERT INTO friends (user_id, friend_id) VALUES (?, ?) ON CONFLICT DO NOTHING", userId, friendId);
        jdbc.update("INSERT INTO friends (user_id, friend_id) VALUES (?, ?) ON CONFLICT DO NOTHING", friendId, userId);
    }

    @Override
    public int removeFriend(int userId, int friendId) {
        // Удаляем дружбу в обе стороны
        int removed1 = jdbc.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ?", userId, friendId);
        int removed2 = jdbc.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ?", friendId, userId);
        return removed1 + removed2;
    }

    @Override
    public List<Integer> getFriendsIds(int userId) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ?";
        return jdbc.query(sql, (rs, rowNum) -> rs.getInt("friend_id"), userId);
    }

    @Override
    public Collection<Integer> getCommonFriends(int userId, int otherId) {
        String sql = """
            SELECT f1.friend_id
            FROM friends f1
            JOIN friends f2 ON f1.friend_id = f2.friend_id
            WHERE f1.user_id = ? AND f2.user_id = ?
        """;
        return jdbc.query(sql, (rs, rowNum) -> rs.getInt("friend_id"), userId, otherId);
    }
}
