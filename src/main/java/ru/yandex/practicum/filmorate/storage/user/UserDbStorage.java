package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbc;
    private final UserRowMapper userRowMapper;
    private final FriendsDbStorage friendsDbStorage;

    @Override
    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        log.info("Создан пользователь: id={}, login={}, email={}", user.getId(), user.getLogin(), user.getEmail());

        return user;
    }

    @Override
    public User update(User user) {
        this.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        // Обновляем данные пользователя в БД
        jdbc.update(
                "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE id=?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId()
        );

        log.info("Обновлён пользователь: id={}, login={}, email={}", user.getId(), user.getLogin(), user.getEmail());
        return user;
    }

    @Override
    public Optional<User> findById(int id) {
        Optional<User> userOpt = jdbc.query(
                "SELECT * FROM users WHERE id=?",
                userRowMapper,
                id
        ).stream().findFirst();

        userOpt.ifPresent(user -> {
            user.setFriends(new HashSet<>(friendsDbStorage.getFriends(user.getId())));
            log.debug("Загружен пользователь {} с {} друзьями", user.getId(), user.getFriends().size());
        });

        if (userOpt.isEmpty()) {
            log.warn("Пользователь с id={} не найден", id);
        }

        return userOpt;
    }

    @Override
    public List<User> findAll() {
        List<User> users = jdbc.query("SELECT * FROM users", userRowMapper);

        for (User user : users) {
            user.setFriends(new HashSet<>(friendsDbStorage.getFriends(user.getId())));
        }

        return users;
    }

    public boolean userExists(int userId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE id = ?",
                Integer.class,
                userId
        );
        return count != null && count > 0;
    }
}