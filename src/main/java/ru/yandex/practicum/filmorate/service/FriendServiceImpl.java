package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.FriendsDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendServiceImpl implements FriendService {

    private final UserStorage userStorage;
    private final FriendsDbStorage friendsDbStorage;

    @Override
    public void addFriend(int userId, int friendId) {

        if (userId == friendId) {
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }

        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + friendId + " не найден"));

        // Двусторонняя дружба
        friendsDbStorage.addFriend(userId, friendId);
        friendsDbStorage.addFriend(friendId, userId);

        log.info("Пользователь {} добавил в друзья {}", userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + friendId + " не найден"));

        try {
            // Удаляем дружбу
            friendsDbStorage.removeFriend(userId, friendId);
            friendsDbStorage.removeFriend(friendId, userId);
        } catch (NotFoundException e) {
            // Игнорируем ошибку, если дружба уже не существует
            log.info("Дружба между {} и {} уже отсутствует", userId, friendId);
        }

        log.info("Пользователь {} удалил из друзей {}", userId, friendId);
    }

    @Override
    public Collection<User> getFriends(int userId) {

        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return friendsDbStorage.getFriends(userId).stream()
                .map(id -> userStorage.findById(id)
                        .orElseThrow(() -> new NotFoundException("Друг с id=" + id + " не найден")))
                .toList();
    }

    @Override
    public Collection<User> getCommonFriends(int userId, int otherId) {

        if (userId == otherId) {
            throw new ValidationException("Нельзя искать общих друзей с самим собой");
        }

        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        userStorage.findById(otherId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return friendsDbStorage.getCommonFriends(userId, otherId).stream()
                .map(id -> userStorage.findById(id)
                        .orElseThrow(() -> new NotFoundException("Друг с id=" + id + " не найден")))
                .toList();
    }
}