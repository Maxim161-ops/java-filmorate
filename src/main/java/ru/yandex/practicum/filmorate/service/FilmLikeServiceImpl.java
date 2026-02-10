package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.film.FilmLikeDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Service
@RequiredArgsConstructor
public class FilmLikeServiceImpl implements FilmLikeService {

    private final FilmLikeDbStorage filmLikeDbStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public void addLike(int filmId, int userId) {

        filmStorage.findById(filmId)
                .orElseThrow(() ->
                        new NotFoundException("Фильм с id=" + filmId + " не найден"));

        userStorage.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("Пользователь с id=" + userId + " не найден"));

        filmLikeDbStorage.addLike(filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {

        filmStorage.findById(filmId)
                .orElseThrow(() ->
                        new NotFoundException("Фильм с id=" + filmId + " не найден"));

        userStorage.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("Пользователь с id=" + userId + " не найден"));

        filmLikeDbStorage.removeLike(filmId, userId);
    }
}
