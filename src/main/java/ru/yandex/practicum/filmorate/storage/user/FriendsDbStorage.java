package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FriendsDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public void addFriend(int userId, int friendId) {
        String sql = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
        jdbcTemplate.update(sql, friendId, userId);
        log.debug("Пользователь {} добавил друга {}", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        int rows = jdbcTemplate.update(sql, userId, friendId);

        if (rows == 0) {
            throw new ru.yandex.practicum.filmorate.exception.NotFoundException(
                    "Дружба между пользователями " + userId + " и " + friendId + " не найдена");
        }

        log.debug("Пользователь {} удалил друга {}", userId, friendId);
    }

    public Set<Integer> getFriends(int userId) {
        Set<Integer> friends = jdbcTemplate.queryForList(
                "SELECT friend_id FROM friends WHERE user_id = ?",
                Integer.class,
                userId
        ).stream().collect(Collectors.toSet());

        log.debug("Загружены {} друзей для пользователя {}", friends.size(), userId);
        return friends;
    }
}