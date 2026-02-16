package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface FriendsStorage {

    void addFriend(int userId, int friendId);

    int removeFriend(int userId, int friendId); // возвращаем количество удалённых записей

    Collection<Integer> getCommonFriends(int userId, int otherId);

    List<Integer> getFriendsIds(int userId);

    List<User> getFriends(int userId);
}
