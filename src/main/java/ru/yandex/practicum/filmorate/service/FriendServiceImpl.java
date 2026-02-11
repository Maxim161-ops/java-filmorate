package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        if (userId == friendId) {
            throw new ValidationException("Нельзя удалить самого себя из друзей");
        }

        checkUserExists(userId);
        checkUserExists(friendId);

        int removed = friendsStorage.removeFriend(userId, friendId);

        if (removed == 0) {
            throw new NotFoundException(
                    "Дружба между пользователями " + userId + " и " + friendId + " не найдена"
            );
        }

        log.info("Пользователь {} удалил из друзей пользователя {}", userId, friendId);
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