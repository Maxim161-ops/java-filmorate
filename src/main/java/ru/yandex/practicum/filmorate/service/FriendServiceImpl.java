package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendServiceImpl implements FriendService {

    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;
    private final JdbcTemplate jdbc;

    @Override
    public void addFriend(int userId, int friendId) {
        checkUserExists(userId);
        checkUserExists(friendId);

        friendsStorage.addFriend(userId, friendId);
        log.info("Пользователь id={} добавил в друзья пользователя id={}", userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        checkUserExists(userId);
        checkUserExists(friendId);

        friendsStorage.removeFriend(userId, friendId);
        log.info("Пользователь id={} удалил из друзей пользователя id={}", userId, friendId);
    }

    @Override
    public Collection<User> getFriends(int userId) {
        checkUserExists(userId);

        Set<User> friends = new HashSet<>();
        for (Integer id : friendsStorage.getFriendsIds(userId)) {
            userStorage.findById(id).ifPresent(friends::add);
        }
        return friends;
    }

    @Override
    public Collection<User> getCommonFriends(int userId, int otherUserId) {
        checkUserExists(userId);
        checkUserExists(otherUserId);

        Set<Integer> friends1 = new HashSet<>(friendsStorage.getFriendsIds(userId));
        Set<Integer> friends2 = new HashSet<>(friendsStorage.getFriendsIds(otherUserId));

        friends1.retainAll(friends2);

        Set<User> commonFriends = new HashSet<>();
        for (Integer id : friends1) {
            userStorage.findById(id).ifPresent(commonFriends::add);
        }
        return commonFriends;
    }

    private void checkUserExists(int userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }
}