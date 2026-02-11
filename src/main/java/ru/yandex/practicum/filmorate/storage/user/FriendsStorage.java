package ru.yandex.practicum.filmorate.storage.user;

import java.util.Collection;
import java.util.List;

public interface FriendsStorage {

    void addFriend(int userId, int friendId);

    int removeFriend(int userId, int friendId); // возвращаем количество удалённых записей

    List<Integer> getFriendsIds(int userId);

    Collection<Integer> getCommonFriends(int userId, int otherId);
}
