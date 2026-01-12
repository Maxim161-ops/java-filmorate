package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import jakarta.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // логика в сервисе
    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    // логика в сервисе
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.findById(id);
    }

    // Добавление в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    // Удаление из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.removeFriend(id, friendId);
    }

    // Список друзей
    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable int id) {
        return userService.getFriends(id);
    }

    // Общие друзья
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
