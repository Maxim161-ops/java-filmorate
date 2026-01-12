package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.Collection;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;


    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User create(User user) {
        User created = userStorage.create(user);
        log.info("Создан пользователь: id={}, login={}", created.getId(), created.getLogin());
        return created;
    }

    @Override
    public User update(User user) {
        User existingUser = userStorage.findById(user.getId());

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
        log.debug("Запрос пользователя с id={}", id);
        return userStorage.findById(id);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        User user = findById(userId);
        User friend = findById(friendId);

        boolean added = user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        if (added) {
            log.info("Пользователь {} добавил в друзья пользователя {}", userId, friendId);
        } else {
            log.info("Пользователь {} уже является другом {}", userId, friendId);
        }
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        User user = findById(userId);
        User friend = findById(friendId);

        boolean removed = user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        if (removed) {
            log.info("Пользователь {} удалил из друзей пользователя {}", userId, friendId);
        } else {
            log.info("Пользователь {} не был другом пользователя {}", userId, friendId);
        }
    }

    @Override
    public Collection<User> getFriends(int userId) {
        User user = findById(userId);
        log.debug("Получен список друзей пользователя {}, count={}", userId, user.getFriends().size());
        return user.getFriends().stream()
                .map(this::findById)
                .toList();
    }

    @Override
    public Collection<User> getCommonFriends(int userId, int otherId) {
        User user = findById(userId);
        User other = findById(otherId);

        log.debug("Вычисляем общих друзей для пользователей {} и {}", userId, otherId);

        return user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .map(this::findById)
                .toList();
    }
}
