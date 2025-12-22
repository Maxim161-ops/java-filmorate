package ru.yandex.practicum.filmorate.controller;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    private void fillNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info(
                    "Имя пользователя пустое — устанавливаем login в качестве имени. login={}",
                    user.getLogin()
            );
            user.setName(user.getLogin());
        }
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        fillNameIfEmpty(user);

        user.setId(nextId++);
        users.put(user.getId(), user);

        log.info("Создан пользователь: id={}, login={}", user.getId(), user.getLogin());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Пользователь не найден: id={}", user.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }
        fillNameIfEmpty(user);
        users.put(user.getId(), user);
        log.info("Обновлён пользователь: {}", user);

        return user;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return users.values();
    }
}
