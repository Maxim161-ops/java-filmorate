package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {

    User create(User user);

    User update(User user);

    Collection<User> findAll();

    User findById(int id);
}