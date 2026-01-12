package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {

    User create(User user);

    User update(User user);

    Collection<User> findAll();

    User findById(int id);

    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    Collection<User> getFriends(int userId);

    Collection<User> getCommonFriends(int userId, int otherId);
}