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

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Name пустой, подставлен login: {}", user.getLogin());
        }

        User created = userStorage.create(user);
        log.info("Создан пользователь: id={}, login={}", created.getId(), created.getLogin());
        return created;
    }

    @Override
    public User update(User user) {
        userStorage.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + user.getId() + " не найден"));

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Name пустой при обновлении, подставлен login: {}", user.getLogin());
        }

        User updated = userStorage.update(user);
        log.info("Обновлён пользователь: id={}, login={}", updated.getId(), updated.getLogin());
        return updated;
    }

    @Override
    public Collection<User> findAll() {
        Collection<User> users = userStorage.findAll();
        log.debug("Получен список всех пользователей, count={}", users.size());
        return users;
    }

    @Override
    public User findById(int id) {
        User user = userStorage.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Пользователь с id=" + id + " не найден")
                );

        user.setFriends(new HashSet<>(friendsStorage.getFriendsIds(id)));

        return user;
    }

    @Override
    public void delete(int id) {
        userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userStorage.delete(id);
    }
}