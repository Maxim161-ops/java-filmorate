package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendServiceImpl implements FriendService {

    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;

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
        return new HashSet<>(friendsStorage.getFriends(userId));
    }

    @Override
    public Collection<User> getCommonFriends(int userId, int otherUserId) {
        checkUserExists(userId);
        checkUserExists(otherUserId);

        // Получаем сразу объекты User для обоих пользователей
        List<User> friendsOfUser1 = friendsStorage.getFriends(userId);
        List<User> friendsOfUser2 = friendsStorage.getFriends(otherUserId);

        // Пересекаем по id
        Set<Integer> friends2Ids = new HashSet<>();
        for (User u : friendsOfUser2) {
            friends2Ids.add(u.getId());
        }

        Set<User> commonFriends = new HashSet<>();
        for (User u : friendsOfUser1) {
            if (friends2Ids.contains(u.getId())) {
                commonFriends.add(u);
            }
        }

        return commonFriends;
    }

    private void checkUserExists(int userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }
}