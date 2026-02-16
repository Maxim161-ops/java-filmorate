package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.film.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Service
@RequiredArgsConstructor
public class FilmLikeServiceImpl implements FilmLikeService {

    private final FilmLikeStorage filmLikeStorage;  // интерфейс
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public void addLike(int filmId, int userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);
        filmLikeStorage.addLike(filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);

        int removed = filmLikeStorage.removeLike(filmId, userId); // теперь возвращает int
        if (removed == 0) {
            throw new NotFoundException(
                    "Лайк пользователя " + userId + " для фильма " + filmId + " не найден"
            );
        }
    }

    private void checkFilmExists(int filmId) {
        filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + filmId + " не найден"));
    }

    private void checkUserExists(int userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }
}