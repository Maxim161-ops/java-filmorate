package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendsDbStorage implements FriendsStorage {

    private final JdbcTemplate jdbc;

    @Override
    public void addFriend(int userId, int friendId) {
        // Сначала проверяем, есть ли уже запись
        String checkSql = "SELECT COUNT(*) FROM friends WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbc.queryForObject(checkSql, Integer.class, userId, friendId);

        if (count != null && count == 0) {
            String sql = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
            jdbc.update(sql, userId, friendId);
        }
    }

    @Override
    public int removeFriend(int userId, int friendId) {
        return jdbc.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ?", userId, friendId);
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

    @Override
    public List<User> getFriends(int userId) {
        String sql = """
        SELECT u.id, u.email, u.login, u.name, u.birthday
        FROM users u
        JOIN friends f ON u.id = f.friend_id
        WHERE f.user_id = ?
    """;

        return jdbc.query(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getObject("birthday", LocalDate.class));
            return user;
        }, userId);
    }
}
