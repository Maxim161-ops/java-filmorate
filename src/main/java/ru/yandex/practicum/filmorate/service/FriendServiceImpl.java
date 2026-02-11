package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendServiceImpl implements FriendService {

    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;
    private final JdbcTemplate jdbc;

    @Override
    public void addFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }

        checkUserExists(userId);
        checkUserExists(friendId);

        friendsStorage.addFriend(userId, friendId);

        log.info("Пользователь {} добавил в друзья пользователя {}", userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        // Сначала проверяем, существует ли пользователь
        String userCheck = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer userExists = jdbc.queryForObject(userCheck, Integer.class, userId);
        if (userExists == null || userExists == 0) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }

        // Проверяем, существует ли друг
        String friendCheck = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer friendExists = jdbc.queryForObject(friendCheck, Integer.class, friendId);
        if (friendExists == null || friendExists == 0) {
            throw new NotFoundException("Пользователь с id=" + friendId + " не найден");
        }

        // Удаляем запись дружбы
        String deleteSql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        int rows = jdbc.update(deleteSql, userId, friendId);

        // Если записи не было — возвращаем 404
        if (rows == 0) {
            throw new NotFoundException("Дружба между пользователями " + userId + " и " + friendId + " не найдена");
        }
    }

    @Override
    public List<User> getFriends(int userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        return friendsStorage.getFriendsIds(userId).stream()
                .map(id -> userStorage.findById(id)
                        .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден")))
                .toList();
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        checkUserExists(userId);
        checkUserExists(otherId);

        return friendsStorage.getCommonFriends(userId, otherId).stream()
                .map(id -> userStorage.findById(id)
                        .orElseThrow(() ->
                                new NotFoundException("Пользователь с id=" + id + " не найден")))
                .toList();
    }

    private void checkUserExists(int userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }
}