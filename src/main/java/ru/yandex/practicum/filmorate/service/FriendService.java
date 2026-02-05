package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;

public interface FriendService {

    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    Collection<User> getFriends(int userId);
    
    Collection<User> getCommonFriends(int userId, int otherId);
}
