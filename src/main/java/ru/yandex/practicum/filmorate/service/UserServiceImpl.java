package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

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
        User existing = userStorage.findById(user.getId()).orElseThrow(() -> new NotFoundException(
                        "Пользователь с id=" + user.getId() + " не найден"));

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
        return userStorage.findById(id).orElseThrow(() ->
                        new NotFoundException("Пользователь с id=" + id + " не найден"));
    }

    @Override
    public void addFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new ValidationException("Пользователь не может добавить самого себя в друзья");
        }
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
        // Проверка: нельзя удалить себя
        if (userId == friendId) {
            log.warn("Попытка удалить себя из друзей: userId={}", userId);
            throw new IllegalArgumentException("Нельзя удалить себя из друзей");
        }

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
        // Проверка: нельзя искать общих друзей с самим собой
        if (userId == otherId) {
            log.warn("Попытка получить общих друзей для одного и того же пользователя: userId={}", userId);
            throw new IllegalArgumentException("Нельзя искать общих друзей с самим собой");
        }

        User user = findById(userId);
        User other = findById(otherId);

        log.debug("Вычисляем общих друзей для пользователей {} и {}", userId, otherId);

        return user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .map(this::findById)
                .toList();
    }
}

/* С прошедшим новым годом !!!!
 и спасибо за помощь в этом не легком деле =)
 */
